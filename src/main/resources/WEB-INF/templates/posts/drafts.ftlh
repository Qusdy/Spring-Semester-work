<#import "../base.ftlh" as base>

<@base.base>
    <h1>Черновики группы: ${group.name}</h1>

    <p><a href="${contextPath}/groups/${group.id}/posts/new" class="btn btn-primary">+ Создать новый пост</a></p>

    <#if drafts?size == 0>
        <p>Нет черновиков. <a href="${contextPath}/groups/${group.id}/posts/new">Создать первый пост?</a></p>
    <#else>
        <table border="1" cellpadding="10" style="width: 100%; border-collapse: collapse;">
            <thead>
            <tr>
                <th>Заголовок</th>
                <th>Создан</th>
                <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            <#list drafts as post>
                <tr>
                    <td><a href="${contextPath}/groups/${group.id}/posts/${post.id}">${post.title}</a></td>
                    <td>${post.createdAt}</td>
                    <td>
                        <a href="${contextPath}/groups/${group.id}/posts/${post.id}/edit">Редактировать</a>

                        <form action="${contextPath}/groups/${group.id}/posts/${post.id}/publish" method="POST" style="display: inline;">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <button type="submit">Опубликовать</button>
                        </form>

                        <form action="${contextPath}/groups/${group.id}/posts/${post.id}/delete" method="POST" style="display: inline;"
                              onsubmit="return confirm('Удалить пост?')">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <button type="submit" style="color: red;">Удалить</button>
                        </form>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </#if>
</@base.base>
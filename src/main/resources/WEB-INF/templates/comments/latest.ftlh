<#import "../base.ftlh" as base>

<@base.base>
    <h1>Последние комментарии к каждому посту</h1>

    <p>Всего комментариев: ${totalItems}</p>

    <#if comments?size == 0>
        <p>Нет комментариев</p>
    <#else>
        <#list comments as comment>
            <div style="border: 1px solid #ddd; margin: 15px 0; padding: 15px; border-radius: 8px;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                    <strong>${comment.author.username}</strong>
                    <small style="color: #999;">${comment.createdAt}</small>
                </div>
                <p style="margin: 10px 0;">${comment.content}</p>
                <a href="/groups/${comment.post.group.id}/posts/${comment.post.id}" style="color: #007bff;">
                    → К посту: ${comment.post.title}
                </a>
            </div>
        </#list>

        <div style="margin-top: 30px; display: flex; justify-content: center; gap: 10px;">
            <#if currentPage gt 0>
                <a href="?page=${currentPage - 1}" style="padding: 8px 12px; background: #007bff; color: white; text-decoration: none; border-radius: 4px;">← Предыдущая</a>
            </#if>

            <span style="padding: 8px 12px;">
                Страница ${currentPage + 1} из ${totalPages}
            </span>

            <#if currentPage + 1 lt totalPages>
                <a href="?page=${currentPage + 1}" style="padding: 8px 12px; background: #007bff; color: white; text-decoration: none; border-radius: 4px;">Следующая →</a>
            </#if>
        </div>
    </#if>

    <div style="margin-top: 20px;">
        <a href="${contextPath}/groups">← На главную</a>
    </div>
</@base.base>
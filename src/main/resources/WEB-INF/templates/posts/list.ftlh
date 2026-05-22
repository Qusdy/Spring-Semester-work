<#import "../base.ftlh" as base>

<@base.base>
    <h1>Последние новости</h1>

    <#if posts?size == 0>
        <p>Новостей пока нет.</p>
    <#else>
        <#list posts as post>
            <article>
                <h2><a href="${contextPath}/groups/${post.group.id}/posts/${post.id}">${post.title}</a></h2>
                <p>
                    Автор: ${post.author.username} |
                    Просмотров: ${post.viewsCount} |
                    ${post.publishedAt}
                    <#if post.group??>
                        | Группа: <a href="${contextPath}/groups/${post.group.id}">${post.group.name}</a>
                    </#if>
                </p>
                <#if post.content?length gt 200>
                    <p>${post.content?substring(0, 200)}...</p>
                <#else>
                    <p>${post.content}</p>
                </#if>
            </article>
        </#list>
        <#if totalPages gt 1>
            <nav>
                <ul style="display: flex; gap: 10px; list-style: none; padding: 0;">
                    <#if currentPage gt 0>
                        <li><a href="?page=${currentPage - 1}&size=10">← Предыдущая</a></li>
                    </#if>

                    <#list 0..totalPages-1 as p>
                        <li>
                            <#if p == currentPage>
                                <strong>${p + 1}</strong>
                            <#else>
                                <a href="?page=${p}&size=10">${p + 1}</a>
                            </#if>
                        </li>
                    </#list>

                    <#if currentPage + 1 lt totalPages>
                        <li><a href="?page=${currentPage + 1}&size=10">Следующая →</a></li>
                    </#if>
                </ul>
            </nav>
        </#if>
    </#if>
</@base.base>
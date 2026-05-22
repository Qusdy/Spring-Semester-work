<#import "../base.ftlh" as base>

<@base.base>
    <h1>Поиск</h1>

    <form method="GET" action="${contextPath}/posts/search">
        <input type="text" name="q" placeholder="Поиск..." value="${query!""}">
        <label>
            <input type="checkbox" name="external" value="true" ${external?then('checked', '')}>
            Внешние
        </label>
        <button type="submit">Найти</button>
    </form>

    <#if posts?size == 0>
        <p>Ничего не найдено</p>
    <#else>
        <#list posts as post>
            <div style="border: 1px solid #ddd; margin: 10px 0; padding: 10px;">
                <#if external>
                    <h3><a href="${post.url}" target="_blank">${post.title}</a></h3>
                    <p>${post.description!""}</p>
                <#else>
                    <h3><a href="${contextPath}/groups/${post.group.id}/posts/${post.id}">${post.title}</a></h3>
                    <#if post.content?length gt 200>
                        <p>${post.content?substring(0, 200)}...</p>
                    <#else>
                        <p>${post.content}</p>
                    </#if>
                </#if>
            </div>
        </#list>

        <#if totalPages gt 1>
            <#if currentPage gt 0>
                <a href="?q=${query!""}&external=${external?c}&page=${currentPage - 1}">← Назад</a>
            </#if>
            <#if currentPage + 1 lt totalPages>
                <a href="?q=${query!""}&external=${external?c}&page=${currentPage + 1}">Вперед →</a>
            </#if>
        </#if>
    </#if>
</@base.base>
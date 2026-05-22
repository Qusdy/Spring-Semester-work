<#import "../base.ftlh" as base>

<@base.base>
    <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 20px;">
        <#if group.logoUrl?? && group.logoUrl != "">
            <img src="${group.logoUrl}" alt="${group.name}"
                 style="width: 100px; height: 100px; object-fit: cover; border-radius: 50%;">
        <#else>
            <img src="/images/default-group-logo.png" alt="${group.name}"
                 style="width: 100px; height: 100px; object-fit: cover; border-radius: 50%;">
        </#if>

        <div>
            <h1>${group.name}</h1>
            <p>${group.description!""}</p>
            <p>Создатель: ${group.owner.username}</p>
        </div>
    </div>

    <#if currentUser?? && (isOwner || currentUser.role == 'ADMIN')>
        <div style="margin-bottom: 20px;">
            <a href="${contextPath}/groups/${group.id}/posts/new" class="btn btn-primary">+ Создать пост</a>
            <a href="${contextPath}/groups/${group.id}/posts/drafts" class="btn btn-secondary">Черновики</a>
        </div>
    </#if>

    <hr>

    <div class="tabs" style="display: flex; gap: 10px; margin-bottom: 20px;">
        <a href="${contextPath}/groups/${group.id}/posts" class="tab">Опубликованные</a>
        <#if currentUser?? && (isOwner || currentUser.role == 'ADMIN')>
            <a href="${contextPath}/groups/${group.id}/posts/drafts" class="tab">Черновики</a>
        </#if>
    </div>

    <h2>Посты группы</h2>

    <#if posts?size == 0>
        <p>В этой группе пока нет опубликованных постов.</p>
    <#else>
        <div class="posts-list">
            <#list posts as post>
                <div class="post-card" style="border: 1px solid #ddd; margin: 15px 0; padding: 15px; border-radius: 8px;">
                    <#if post.imageUrl?? && post.imageUrl != "/images/default-post-image.png">
                        <img src="${post.imageUrl}" alt="${post.title}"
                             style="max-width: 100%; max-height: 200px; object-fit: cover;">
                    </#if>
                    <h3><a href="${contextPath}/groups/${group.id}/posts/${post.id}">${post.title}</a></h3>
                    <p>
                        Автор: ${post.author.username} |
                        <#if post.publishedAt??>${post.publishedAt}<#else>Не опубликован</#if>
                        | Просмотров: ${post.viewsCount}
                    </p>
                    <#if post.content?length gt 200>
                        <p>${post.content?substring(0, 200)}...</p>
                    <#else>
                        <p>${post.content}</p>
                    </#if>
                    <a href="${contextPath}/groups/${group.id}/posts/${post.id}">Читать далее →</a>
                </div>
            </#list>
        </div>
    </#if>
</@base.base>
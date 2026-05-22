<#import "../base.ftlh" as base>

<@base.base>
    <h1>${title}</h1>

    <form action="${contextPath}/groups/${groupId}/posts" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <#if post.id??>
            <input type="hidden" name="_method" value="PUT">
        </#if>

        <div>
            <label>Заголовок:</label>
            <input type="text" name="title" value="${post.title!""}" required style="width: 100%; padding: 8px;">
        </div>

        <div>
            <label>Содержание:</label>
            <textarea name="content" rows="10" required style="width: 100%; padding: 8px;">${post.content!""}</textarea>
        </div>

        <div>
            <label>Изображение:</label>
            <input type="file" name="imageFile" accept="image/jpeg,image/png,image/gif,image/webp">
            <#if post.imageUrl?? && post.imageUrl != "/images/default-post-image.png">
                <p>Текущее изображение:</p>
                <img src="${post.imageUrl}" style="max-width: 200px;">
            </#if>
        </div>

        <button type="submit">Сохранить как черновик</button>
        <a href="${contextPath}/groups/${groupId}/posts">Отмена</a>
    </form>
</@base.base>
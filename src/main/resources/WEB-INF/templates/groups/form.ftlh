<#import "../base.ftlh" as base>

<@base.base>
    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <h1 class="mb-4">${title}</h1>

                <#if group.id??>
                <form action="${contextPath}/groups/${group.id}" method="POST" enctype="multipart/form-data"></form>
                    <#else>
                    <form action="${contextPath}/groups" method="POST" enctype="multipart/form-data">
                        </#if>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                        <div class="mb-3">
                            <label class="form-label">Название группы</label>
                            <input type="text" class="form-control" name="name" value="${group.name!""}" required maxlength="100">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Описание</label>
                            <textarea class="form-control" name="description" rows="5">${group.description!""}</textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Логотип</label>
                            <input type="file" class="form-control" name="logoFile" accept="image/jpeg,image/png,image/gif,image/webp">
                            <div class="form-text">JPEG, PNG, GIF, WEBP до 10MB</div>

                            <#if group.logoUrl?? && group.logoUrl != "">
                                <div class="mt-2">
                                    <p class="mb-1">Текущий логотип:</p>
                                    <img src="${group.logoUrl}" class="img-thumbnail" style="max-width: 100px;">
                                </div>
                            </#if>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary">Сохранить</button>
                            <a href="${contextPath}/groups" class="btn btn-secondary">Отмена</a>
                        </div>
                    </form>
            </div>
        </div>
    </div>
</@base.base>
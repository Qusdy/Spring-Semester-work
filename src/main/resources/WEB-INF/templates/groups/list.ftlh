<#import "../base.ftlh" as base>

<@base.base>
    <h1>Все группы</h1>

    <#if currentUser??>
        <p><a href="${contextPath}/groups/new">Создать новую группу</a></p>
    </#if>

    <#if groups?size == 0>
        <p>Групп пока нет. Будьте первым, кто создаст группу!</p>
    <#else>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;">
            <#list groups as group>
                <div style="border: 1px solid #ddd; border-radius: 8px; padding: 15px;">
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <#if group.logoUrl??>
                            <img src="${group.logoUrl}" alt="${group.name}"
                                 style="width: 50px; height: 50px; object-fit: cover; border-radius: 50%;">
                        </#if>
                        <h2 style="margin: 0;"><a href="${contextPath}/groups/${group.id}">${group.name}</a></h2>
                    </div>
                    <p>${group.description!""}</p>
                    <p>Создатель: ${group.owner.username}</p>
                </div>
            </#list>
        </div>
    </#if>
</@base.base>
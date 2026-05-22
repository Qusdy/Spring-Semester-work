<#macro base>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="_csrf" content="${_csrf.token}">
        <meta name="_csrf_header" content="${_csrf.headerName}">
        <title><#if title??>${title} | </#if>Новостная платформа</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    </head>
    <body>
    <header>
        <nav style="display: flex; gap: 15px; padding: 10px; background: #f0f0f0;">
            <a href="${contextPath}/groups">Группы</a>
            <a href="${contextPath}/posts/search">Поиск</a>

            <#if currentUser??>
                <a href="${contextPath}/comments/latest">Последние комментарии</a>
                <a href="${contextPath}/notifications">Уведомления <span id="notification-badge">0</span></a>
                <a href="${contextPath}/groups/new">Создать группу</a>

                <span>${currentUser.username} (${currentUser.role})</span>

                <#if currentUser.role == 'ADMIN'>
                    <a href="${contextPath}/admin/users">Админ-панель</a>
                </#if>

                <form action="${contextPath}/logout" method="POST" style="display: inline;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <button type="submit">Выйти</button>
                </form>
            <#else>
                <a href="${contextPath}/login">Войти</a>
                <a href="${contextPath}/register">Регистрация</a>
            </#if>
        </nav>
    </header>

    <main style="padding: 20px; max-width: 1200px; margin: 0 auto;">
        <#if successMessage??>
            <div style="color: green; padding: 10px; margin: 10px 0; background: #e8f5e9; border-radius: 5px;">
                ${successMessage}
            </div>
        </#if>
        <#if errorMessage??>
            <div style="color: red; padding: 10px; margin: 10px 0; background: #ffebee; border-radius: 5px;">
                ${errorMessage}
            </div>
        </#if>

        <#nested>
    </main>

    <script>
        function updateNotificationCount() {
            $.get("/notifications/unread-count", function(count) {
                $("#notification-badge").text(count > 0 ? count : "0");
            });
        }
        $(document).ready(function() {
            updateNotificationCount();
            setInterval(updateNotificationCount, 30000);
        });
    </script>
    </body>
    </html>
</#macro>
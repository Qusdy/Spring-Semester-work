<#import "../base.ftlh" as base>

<@base.base>
    <h1>Уведомления</h1>

    <div style="margin-bottom: 20px;">
        <button onclick="markAllAsRead()" style="padding: 8px 16px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">
            Отметить все как прочитанные
        </button>
    </div>

    <#if notifications?size == 0>
        <div style="text-align: center; padding: 50px; color: #999;">
            <p>У вас нет уведомлений</p>
        </div>
    <#else>
        <div id="notifications-list">
            <#list notifications as notification>
                <div class="notification-item"
                     id="notif-${notification.id}"
                     style="border: 1px solid #e0e0e0;
                             margin: 10px 0;
                             padding: 15px;
                             border-radius: 8px;
                             background-color: ${notification.isRead?then('white', '#f0f8ff')};
                             transition: all 0.2s;">

                    <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                        <div style="flex: 1;">
                            <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 8px;">
                                <h3 style="margin: 0; font-size: 16px;">${notification.title}</h3>
                            </div>
                            <p style="margin: 5px 0; color: #555;">${notification.message}</p>
                            <small style="color: #999;">${notification.createdAt}</small>
                        </div>

                        <div style="display: flex; flex-direction: column; gap: 8px;">
                            <#if !notification.isRead>
                                <button onclick="markAsRead(${notification.id})"
                                        style="padding: 4px 12px; background: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 12px;">
                                    ✓ Прочитано
                                </button>
                            </#if>

                            <#if notification.referenceId?? && notification.referenceType == 'POST'>
                                <a href="${contextPath}/notifications/posts/${notification.referenceId}"
                                   style="padding: 4px 12px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; text-align: center; font-size: 12px;">
                                    → Перейти
                                </a>
                            </#if>
                        </div>
                    </div>
                </div>
            </#list>
        </div>
    </#if>

    <script>
        var csrfToken = '${_csrf.token}';
        var csrfHeader = '${_csrf.headerName}';

        $(document).ajaxSend(function(e, xhr, options) {
            if (options.type === 'POST') {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        });

        function markAsRead(notificationId) {
            $.post("${contextPath}/notifications/" + notificationId + "/read", function() {
                $("#notif-" + notificationId).css("background-color", "white");
                $("#notif-" + notificationId + " button:contains('Прочитано')").remove();
                updateNotificationCount();
            }).fail(function() {
                alert("Ошибка при отметке прочитанным");
            });
        }

        function markAllAsRead() {
            $.post("${contextPath}/notifications/read-all", function() {
                $(".notification-item").css("background-color", "white");
                $(".notification-item button:contains('Прочитано')").remove();
                updateNotificationCount();
                $("#notification-badge").text("0");
            }).fail(function() {
                alert("Ошибка при отметке всех прочитанными");
            });
        }

        function updateNotificationCount() {
            $.get("${contextPath}/notifications/unread-count", function(count) {
                $("#notification-badge").text(count > 0 ? count : "0");
            });
        }
    </script>
</@base.base>
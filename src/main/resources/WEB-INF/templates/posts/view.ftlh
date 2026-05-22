<#import "../base.ftlh" as base>

<@base.base>
    <script>
        var csrfToken = '${_csrf.token}';
        var csrfHeader = '${_csrf.headerName}';

        $(document).ajaxSend(function(e, xhr, options) {
            if (options.type !== 'GET') {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        });
    </script>

    <div style="margin-bottom: 20px;">
        <a href="${contextPath}/groups/${group.id}/posts">← Назад к постам группы</a>
    </div>

    <article>
        <h1>${post.title}</h1>

        <p>
            Автор: ${post.author.username} |
            <#if post.publishedAt??>${post.publishedAt}<#else>Черновик</#if> |
            Просмотров: ${post.viewsCount}
        </p>

        <#if post.imageUrl?? && post.imageUrl != "/images/default-post-image.png">
            <img src="${post.imageUrl}" alt="${post.title}" style="max-width: 100%;">
        </#if>

        <div>
            ${post.content!""}
        </div>

        <#if currentUser?? && (isOwner || currentUser.role == 'ADMIN')>
            <div style="margin-top: 20px;">
                <a href="${contextPath}/groups/${group.id}/posts/${post.id}/edit">Редактировать</a>

                <#if post.status == 'DRAFT'>
                    <form action="${contextPath}/groups/${group.id}/posts/${post.id}/publish" method="POST" style="display: inline;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        <button type="submit">Опубликовать</button>
                    </form>
                </#if>

                <form action="${contextPath}/groups/${group.id}/posts/${post.id}/delete" method="POST" style="display: inline;"
                      onsubmit="return confirm('Удалить пост?')">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <button type="submit" style="color: red;">Удалить</button>
                </form>
            </div>
        </#if>

        <p><strong>Статус:</strong>
            <#if post.status == 'DRAFT'>Черновик
            <#elseif post.status == 'PUBLISHED'>Опубликован
            <#else>${post.status}
            </#if>
        </p>
    </article>

    <hr>

    <h3>Теги</h3>

    <div id="tags-container">
        <#if post.tags?? && post.tags?size gt 0>
            <#list post.tags as tag>
                <span id="tag-${tag.id}" class="tag">
                ${tag.name}
                    <#if currentUser?? && (isOwner || currentUser.role == 'ADMIN')>
                        <button onclick="removeTag(${post.id}, ${tag.id})" class="remove-tag">✖</button>
                    </#if>
            </span>
            </#list>
        <#else>
            <p id="no-tags-message">Нет тегов</p>
        </#if>
    </div>

    <#if currentUser?? && (isOwner || currentUser.role == 'ADMIN')>
        <div style="margin-top: 15px;">
            <input type="text" id="tag-input" placeholder="Введите теги через запятую (java, spring, news)"
                   style="padding: 8px; width: 300px; border: 1px solid #ddd; border-radius: 4px;">
            <button onclick="addTags(${post.id})" style="padding: 8px 16px; background: #28a745; color: white; border: none; border-radius: 4px;">+ Добавить теги</button>
        </div>
        <div id="tag-suggestions" style="position: absolute; background: white; border: 1px solid #ddd; max-height: 150px; overflow-y: auto; display: none;"></div>
    </#if>

    <hr>

    <hr>

    <section id="comments-section">
        <h2>Комментарии (<span id="comments-count">${comments?size}</span>)</h2>

        <#if currentUser??>
            <div>
                <textarea id="comment-content" placeholder="Ваш комментарий..." rows="3" style="width: 100%; padding: 8px;"></textarea>
                <button onclick="addComment(${post.id})">Отправить</button>
            </div>
        <#else>
            <p><a href="${contextPath}/login">Войдите</a>, чтобы оставить комментарий.</p>
        </#if>

        <div id="comments-list">
            <#list comments as comment>
                <div class="comment" id="comment-${comment.id}" style="border: 1px solid #ddd; margin: 10px 0; padding: 10px;">
                    <strong>${comment.author.username}</strong>
                    <small>${comment.createdAt}</small>
                    <p class="comment-content-${comment.id}">${comment.content}</p>
                    <#if currentUser?? && (currentUser.id == comment.author.id || currentUser.role == 'ADMIN')>
                        <button onclick="editComment(${comment.id})">Редактировать</button>
                        <button onclick="deleteComment(${comment.id})">Удалить</button>
                    </#if>
                </div>
            </#list>
        </div>
    </section>

    <script>
        var currentUserId = ${(currentUser??)?then(currentUser.id, 0)};
        var isAdmin = ${(currentUser?? && currentUser.role == 'ADMIN')?c};

        function escapeHtml(text) {
            if (!text) return '';
            return text.replace(/[&<>]/g, function(m) {
                if (m === '&') return '&amp;';
                if (m === '<') return '&lt;';
                if (m === '>') return '&gt;';
                return m;
            });
        }

        function addComment(postId) {
            var content = $("#comment-content").val();
            if (!content || !content.trim()) return;

            $.ajax({
                url: "${contextPath}/api/posts/" + postId + "/comments",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({content: content}),
                success: function(comment) {
                    var canEdit = (currentUserId === comment.authorId || isAdmin);
                    var buttons = canEdit ?
                        '<button onclick="editComment(' + comment.id + ')">Редактировать</button> ' +
                        '<button onclick="deleteComment(' + comment.id + ')">Удалить</button>' : '';

                    var commentHtml = '<div class="comment" id="comment-' + comment.id + '" style="border: 1px solid #ddd; margin: 10px 0; padding: 10px;">' +
                        '<strong>' + escapeHtml(comment.authorUsername) + '</strong>' +
                        '<small> ' + new Date(comment.createdAt).toLocaleString() + '</small>' +
                        '<p class="comment-content-' + comment.id + '">' + escapeHtml(comment.content) + '</p>' +
                        buttons +
                        '</div>';
                    $("#comments-list").prepend(commentHtml);
                    $("#comment-content").val("");

                    var countSpan = $("#comments-count");
                    if (countSpan.length) {
                        countSpan.text(parseInt(countSpan.text()) + 1);
                    }
                },
                error: function(xhr) {
                    alert("Ошибка: " + (xhr.responseText || "неизвестная ошибка"));
                }
            });
        }

        function editComment(commentId) {
            var currentContent = $(".comment-content-" + commentId).text();
            var newContent = prompt("Редактировать комментарий:", currentContent);
            if (!newContent || !newContent.trim()) return;

            $.ajax({
                url: "${contextPath}/api/comments/" + commentId,
                type: "PUT",
                contentType: "application/json",
                data: JSON.stringify({content: newContent}),
                success: function(comment) {
                    $(".comment-content-" + comment.id).text(escapeHtml(comment.content));
                },
                error: function(xhr) {
                    alert("Ошибка редактирования");
                }
            });
        }

        function deleteComment(commentId) {
            if (!confirm("Удалить комментарий?")) return;

            $.ajax({
                url: "${contextPath}/api/comments/" + commentId,
                type: "DELETE",
                success: function() {
                    $("#comment-" + commentId).remove();
                    var countSpan = $("#comments-count");
                    if (countSpan.length) {
                        countSpan.text(Math.max(0, parseInt(countSpan.text()) - 1));
                    }
                },
                error: function(xhr) {
                    alert("Ошибка удаления");
                }
            });
        }

        $(document).ready(function() {
            var postId = ${post.id};
            $.get("${contextPath}/api/posts/" + postId + "/comments", function(comments) {
                $("#comments-list").empty();
                comments.forEach(function(comment) {
                    var canEdit = (currentUserId === comment.authorId || isAdmin);
                    var buttons = canEdit ?
                        '<button onclick="editComment(' + comment.id + ')">Редактировать</button> ' +
                        '<button onclick="deleteComment(' + comment.id + ')">Удалить</button>' : '';

                    var commentHtml = '<div class="comment" id="comment-' + comment.id + '" style="border: 1px solid #ddd; margin: 10px 0; padding: 10px;">' +
                        '<strong>' + escapeHtml(comment.authorUsername) + '</strong>' +
                        '<small> ' + new Date(comment.createdAt).toLocaleString() + '</small>' +
                        '<p class="comment-content-' + comment.id + '">' + escapeHtml(comment.content) + '</p>' +
                        buttons +
                        '</div>';
                    $("#comments-list").append(commentHtml);
                });
                $("#comments-count").text(comments.length);
            });
        });
    </script>

    <script>
        function addTags(postId) {
            var tagsInput = $("#tag-input").val();
            if (!tagsInput.trim()) return;

            $.ajax({
                url: "${contextPath}/api/tags/post/" + postId,
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({tags: tagsInput}),
                success: function(response) {
                    if (response.success) {
                        renderTags(response.tags, postId);
                        $("#tag-input").val("");
                    }
                },
                error: function(xhr) {
                    alert("Ошибка: " + (xhr.responseJSON?.error || "неизвестная"));
                }
            });
        }

        function removeTag(postId, tagId) {
            $.ajax({
                url: "${contextPath}/api/tags/post/" + postId + "/tag/" + tagId,
                type: "DELETE",
                success: function() {
                    $("#tag-" + tagId).remove();
                    if ($("#tags-container .tag").length === 0) {
                        $("#tags-container").html('<p id="no-tags-message">Нет тегов</p>');
                    }
                }
            });
        }

        function renderTags(tags, postId) {
            var container = $("#tags-container");
            container.empty();

            if (tags.length === 0) {
                container.html('<p id="no-tags-message">Нет тегов</p>');
            } else {
                var html = '';
                var canEdit = ${(currentUser?? && (isOwner || currentUser.role == 'ADMIN'))?c};
                tags.forEach(function(tag) {
                    var removeBtn = canEdit ? '<button onclick="removeTag(' + postId + ', ' + tag.id + ')" class="remove-tag">✖</button>' : '';
                    html += '<span id="tag-' + tag.id + '" class="tag">' + escapeHtml(tag.name) + removeBtn + '</span>';
                });
                container.html(html);
            }
        }

        $(document).ready(function() {
            var postId = ${post.id};
            $.get("${contextPath}/api/tags/post/" + postId, function(tags) {
                renderTags(tags, postId);
            });
        });
    </script>
</@base.base>
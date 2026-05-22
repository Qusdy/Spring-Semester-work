<#import "../base.ftlh" as base>

<@base.base>
    <h1>Регистрация</h1>

    <form action="${contextPath}/register" method="POST">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div>
            <label>Имя пользователя:</label>
            <input type="text" name="username" required>
        </div>

        <div>
            <label>Пароль:</label>
            <input type="password" name="passwordHash" minlength="" required>
        </div>

        <div>
            <label>Подтвердите пароль:</label>
            <input type="password" name="passwordConfirm" required>
        </div>

        <button type="submit">Зарегистрироваться</button>
    </form>

    <p>Уже есть аккаунт? <a href="${contextPath}/login">Войдите</a></p>
</@base.base>
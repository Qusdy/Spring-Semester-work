<#import "../base.ftlh" as base>

<@base.base>
    <h1>Вход</h1>

    <form action="${contextPath}/login" method="POST">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div>
            <label>Username: </label>
            <input type="text" name="username" required maxlength="50">
        </div>

        <div>
            <label>Пароль:</label>
            <input type="password" name="password" required>
        </div>

        <div>
            <label>
                <input type="checkbox" name="remember-me"> Запомнить меня
            </label>
        </div>

        <button type="submit">Войти</button>
    </form>

    <p>Нет аккаунта? <a href="${contextPath}/register">Зарегистрируйтесь</a></p>
</@base.base>
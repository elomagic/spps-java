<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>SPPS - Secret Encryption WebTool</title>
        <link rel="stylesheet" href="css/spps.css">
    </head>
    <body>
        <h1>SPPS Web Encryption Tool - Error</h1>

        <div class="alert warn">It is recommended to disable or to remove this web application after using it!</div>

        <div class="alert error">${errorText}</div>

        <hr/>

        <div class="footer">
            <span>Powered by SPPS ${project.version} (Build date ${build.date}) - <a href="https://github.com/elomagic/spps-java">https://github.com/elomagic/spps-java</a></span>
        </div>
    </body>
</html>

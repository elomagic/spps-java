<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SPPS - Secret Encryption WebTool</title>
    <style>
        body {
            font-family: sans-serif;
        }

        .mono {
            font-family: "Lucida Console", monospace;
        }

        .co {
            color: darkorange;
        }

        .footer {
            font-size: x-small;
        }

        .mb-10 {
            margin-bottom: 10px;
        }
    </style>
</head>
    <body>
        <h3>SPPS Web Encryption Tool</h3>

        <form name="encryptSecretForm" action="encrypt" method="POST">
            <div>
                <label>Secret to encrypt:</label>
                <input type="password" name="secret"/>
            </div>

            <div class="mb-10">
                <input type="submit" value="Encrypt" name="find"/>
                <input type="reset" value="Reset" name="reset" />
            </div>

            <div>
                <label>Encrypted secret:</label>
                <span class="mono co">${encryptedSecret}</span>
            </div>

            <hr/>

            <div class="footer">
                <span>Powered by SPPS ${project.version} - <a href="https://github.com/elomagic/spps-java">https://github.com/elomagic/spps-java</a></span>
            </div>
        </form>
    </body>
</html>

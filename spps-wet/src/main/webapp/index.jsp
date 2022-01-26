<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SPPS - Secret Encryption WebTool</title>
    <link rel="stylesheet" href="css/spps.css">
</head>
    <body>
        <h1>SPPS Web Encryption Tool</h1>

        <div class="alert warn">It is recommended to disable or to remove this web application after using it!</div>

        <form name="encryptSecretForm" action="encrypt" method="POST">
            <div class="mb-10">
                <label for="encryptSecret">Secret to encrypt:</label>
                <input class="form-control" type="password" id="encryptSecret" name="encryptSecret"/>
            </div>

            <div class="mb-10">
                <input class="btn" type="submit" value="Encrypt Secret" name="encrypt"/>
                <input class="btn" type="reset" value="Reset" name="reset" />
            </div>
        </form>

        <div class="mono alert success ${encryptedSecret == null ? 'hide' : ''}">${encryptedSecret}</div>

        <div class="mono alert error ${encryptErrorText == null ? 'hide' : ''}">${encryptErrorText}</div>

        <hr/>

        <form name="generatePrivateKeyForm" action="generate" method="POST">
            <div class="mb-10">
                <input class="btn" type="submit" value="Generate Private Key" name="generate"/>
            </div>
        </form>

        <div class="mono alert success ${generateResultText == null ? 'hide' : ''}">${generateResultText}</div>

        <div class="mono alert error ${generateErrorText == null ? 'hide' : ''}">${generateErrorText}</div>

        <hr/>

        <form name="importSecretForm" action="import" method="POST">
            <div class="mb-10">
                <label for="importKey">Private key to import:</label>
                <input class="form-control" type="password" id="importKey" name="importKey"/>
                <input type="checkbox" id="importForce" name="importForce"/>
                <label for="importForce">Force Import</label>
            </div>

            <div class="mb-10">
                <input class="btn" type="submit" value="Import Key" name="import"/>
                <input class="btn" type="reset" value="Reset" name="reset" />
            </div>
        </form>

        <div class="mono alert success ${importResultText == null ? 'hide' : ''}">${importResultText}</div>

        <div class="mono alert error ${importErrorText == null ? 'hide' : ''}">${importErrorText}</div>

        <hr/>

        <div class="footer">
            <span>Powered by SPPS ${project.version} (Build date ${build.date}) - <a href="https://github.com/elomagic/spps-java">https://github.com/elomagic/spps-java</a></span>
        </div>
    </body>
</html>

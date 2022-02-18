<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SPPS - Secret Encryption WebTool</title>
    <link rel="stylesheet" href="css/spps.css">
</head>
    <body>
        <header>
            <h1>SPPS Web Encryption Tool</h1>
        </header>

        <div class="alert warn">⚠️ It is recommended to disable or to remove this web application after using it! ⚠️</div>

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
                <label for="encryptSecret">Generator Mode:</label>
                <select class="form-select" name="generateMode" id="generateMode">
                    <option value="generate-and-import" >Generate and import w/o print(Recommended mode)</option>
                    <option value="generate-import-and-print">Generate, import and print key</option>
                    <option value="print-only">Generate and print only key w/o import</option>
                </select>
                <input type="checkbox" id="generateAndImportForce" name="importForce"/>
                <label for="generateAndImportForce">Force Import</label>
            </div>

            <div class="mb-10">
                <input class="btn" type="submit" value="Generate Private Key" name="generate"/>
            </div>
        </form>

        <div class="mono alert success ${generateResultText == null ? 'hide' : ''}">
            ${generateResultText}
        </div>

        <div class="mono alert success ${generateResultKey == null ? 'hide' : ''}">
            ${generateResultKey}
        </div>

        <div class="mono alert error ${generateErrorText == null ? 'hide' : ''}">
            ${generateErrorText}
        </div>

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

        <footer class="footer">
            <span>Powered by SPPS ${project.version} (Build date ${timestamp}) - <a href="https://github.com/elomagic/spps-java">https://github.com/elomagic/spps-java</a></span>
        </footer>
    </body>
</html>

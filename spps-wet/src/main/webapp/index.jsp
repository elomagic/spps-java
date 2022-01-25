<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SPPS - Secret Encryption WebTool</title>
    <style>
        body {
            font-family: system-ui,-apple-system,"Segoe UI",Roboto,"Helvetica Neue",Arial,"Noto Sans","Liberation Sans", sans-serif,"Apple Color Emoji","Segoe UI Emoji","Segoe UI Symbol","Noto Color Emoji";
            font-size: 1rem;
        }

        hr {
            border: 1px solid #ced4da;
        }

        h1 {
            font-weight: 500;
            line-height: 1.2;
        }

        .form-control {
            border-radius: .2rem;
            border: 1px solid #ced4da;
            padding: .25rem .5rem;
        }

        .btn {
            margin: .25rem .125rem;
            padding: .25rem .5rem;
            font-size: .875rem;
            cursor: pointer;
            border-radius: .2rem;
            border: 1px solid lightgray;
            // border-color: lightgray;
        }

        .mono {
            font-family: "Lucida Console", monospace;
        }

        .hide {
            display: none;
        }

        .alert {
            position: relative;
            padding: 1rem 1rem;
            margin-bottom: 1rem;
            border: 1px solid transparent;
            border-radius: .25rem;
        }

        .info {
            color: #084298;
            background-color: #cfe2ff;
            border-color: #b6d4fe;
        }

        .success {
            color: #0f5132;
            background-color: #d1e7dd;
            border-color: #badbcc;
        }

        .warn {
            color: #664d03;
            background-color: #fff3cd;
            border-color: #ffecb5;
        }

        .error {
            color: #842029;
            background-color: #f8d7da;
            border-color: #f5c2c7;
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
            <span>Powered by SPPS ${project.version} - <a href="https://github.com/elomagic/spps-java">https://github.com/elomagic/spps-java</a></span>
        </div>
    </body>
</html>

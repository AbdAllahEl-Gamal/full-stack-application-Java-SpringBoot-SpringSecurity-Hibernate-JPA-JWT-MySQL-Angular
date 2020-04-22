<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
 xmlns:th="http://www.thymeleaf.org" style="height: 100%;">
    <head> <title>Reset Password</title> 
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet">
		<style>
			body {
				font-family: 'Montserrat', sans-serif;
			}
			
			.reset-password-button:focus, .reset-password-button:hover {
				background: rgb(1, 29, 51);
				border-color: none;
				outline: none;
			}	
		</style>
	</head>
    <body style="height: 100%; margin: 0 auto; font-size: 18px;">
		<div class="my-container" style="background: #011d33; height: 100%;">
			<div class="rest-password-div" style="display: table; width: 400px; height: 100%; margin: 0 auto; font-size: 16px;">
				<form action="/api/auth/reset-password" th:object="${user}" method="post" style="display: table-cell; vertical-align: middle; background: #ebebeb; padding: 12px;">
					<img alt="" src="data:image/png;base64,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" style="display: block; width: 80%; margin: 0 auto; margin-bottom: 15px;">

					<label for="email" style="display: block;">
					  <input th:field="*{email}" type="text" placeholder="Email" id="email" name="email" value="${email}" autocomplete="off" style="box-sizing: border-box; display: block; width: 100%; border-width: 1px; border-style: solid; padding: 16px; outline: 0; font-size: 0.95em; margin-top: 13px;" readonly>
					</label>
				  
					<label for="password" style="display: block;">
					  <input th:field="*{password}" type="password" placeholder="Password" id="password" name="password" autocomplete="off" style="box-sizing: border-box; display: block; width: 100%; border-width: 1px; border-style: solid; padding: 16px; outline: 0; font-size: 0.95em; margin-top: 13px;">
					</label>

					<input type="submit" value="Submit" class="reset-password-button" style=" background: rgb(1, 29, 51); border-color: transparent; color: #fff; cursor: pointer; font-family: 'Montserrat'; width: 100%; padding: 10px; text-transform: uppercase; font-weight: bold; margin-top: 13px">
					
				</form>
			</div>
		</div>
    </body>
</html>
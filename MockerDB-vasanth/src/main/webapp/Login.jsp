<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Login</title>
    <link
      href="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
      rel="stylesheet"
      id="bootstrap-css"
    />
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
      <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
      integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A=="
      crossorigin="anonymous"
      referrerpolicy="no-referrer"
    />
    <!-- <link rel="stylesheet" href="loginstyle.css"> -->
    <style>
      /* BASIC */

      html {
        background-color: #56baed;
      }

      body {
        font-family: "Poppins", sans-serif;
        height: 100vh;
      }

      a {
        color: #92badd;
        display: inline-block;
        text-decoration: none;
        font-weight: 400;
      }

      h2 {
        text-align: center;
        font-size: 16px;
        font-weight: 600;
        text-transform: uppercase;
        display: inline-block;
        margin: 40px 8px 10px 8px;
        color: #cccccc;
      }
      h3 {
        text-align: center;
        font-size: 30px;
        font-weight: 600;
        text-transform: uppercase;
        display: inline-block;
        margin: 40px 8px 10px 8px;
      }

      /* STRUCTURE */

      .wrapper {
        display: flex;
        align-items: center;
        flex-direction: column;
        justify-content: center;
        width: 100%;
        min-height: 100%;
        padding: 20px;
      }

      #formContent {
        -webkit-border-radius: 10px 10px 10px 10px;
        border-radius: 10px 10px 10px 10px;
        background: #fff;
        padding: 30px;
        width: 90%;
        max-width: 450px;
        position: relative;
        padding: 0px;

        box-shadow: 0 30px 60px 0 rgba(0, 0, 0, 0.3);
        text-align: center;
      }

      #formFooter {
        background-color: #f6f6f6;
        border-top: 1px solid #dce8f1;
        padding: 25px;
        text-align: center;
        -webkit-border-radius: 0 0 10px 10px;
        border-radius: 0 0 10px 10px;
      }

      /* TABS */

      h2.inactive {
        color: #cccccc;
      }

      h2.active {
        color: #0d0d0d;
        border-bottom: 2px solid #5fbae9;
      }

      /* FORM TYPOGRAPHY*/

      input[type="button"],
      input[type="submit"],
      input[type="reset"] {
        background-color: #56baed;
        border: none;
        color: white;
        padding: 15px 80px;
        text-align: center;
        text-decoration: none;
        display: inline-block;

        font-size: 13px;

        margin: 5px 20px 40px 20px;

      }




      input[type="text"],
      input[type="password"] {
        background-color: #f6f6f6;
        border: none;
        color: #0d0d0d;
        padding: 15px 32px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 5px;
        width: 85%;
        border: 2px solid #f6f6f6;

        border-radius: 5px 5px 5px 5px;
      }



      input[type="text"]::placeholder,
      input[type="password"]::placeholder {
        color: #cccccc;
      }

      /* OTHERS */

      *:focus {
        outline: none;
      }

      #icon {
        text-align: center;
        height: 90px;
        width: 80px;
        margin-top: 5px;
        margin-bottom: 7px;
      }
      #nodec {
        color: white;
        text-decoration: none;
        padding-left: 3px;
      }
    </style>
  </head>
  <body>
    <!------ Include the above in your HEAD tag ---------->

    <div class="wrapper fadeInDown">
      <div class="fadeIn first"></div>

      <div id="formContent">
        <!-- Tabs Titles -->

        <!-- Icon -->

        <h3>Login</h3>

        <!-- Login Form -->
        <form id="loginForm">
          <input
            type="text"
            id="username"
            class="fadeIn second"
            name="username"
            placeholder="username"
          />
          <input
            type="password"
            id="password"
            class="fadeIn third"
            name="password"
            placeholder="password"
          />
          <input type="submit" class="fadeIn fourth" value="Log In" />
        </form>
        <a href="/MockerDB-vasanth/register" class="btn btn-dark mb-2">Register</a>
      </div>
    </div>
  </body>
    <script type="text/javascript"><%@include file="/WEB-INF/login.js" %></script><div>

</html>

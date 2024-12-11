$(document).ready(function () {
  $("#loginForm").submit(function (e) {
    e.preventDefault();

    let username = $("#username").val();
    let password = $("#password").val();

    $.ajax({
      type: "POST",
      url: "login",
      data: { username: username, password: password },
      dataType: "json",  // Expect JSON response
      success: function (response) {
        if (response.status === "success") {

          window.location.href = response.redirect;
        } else {
          alert(response.message);  // Display error message
        }
      },
     error: function (jqXHR, textStatus, errorThrown) {

             // window.location.href = "DynamicForm/login";
             window.location.reload();

           console.log("error in logging");
         },
    });
  });
});

$("document").ready(function () {
  $("#registerForm").submit(function (e) {
    e.preventDefault();

    let username = $("#username").val();
    let password = $("#password").val();

    alert(username + " " + password);
    $.ajax({
      type: "POST",
      url: "register",
      data: { username: username, password: password },
      success: function (response) {
      alert(response);
      },
      error: function () {
        console.log("error creating new user");
      },
    });
  });
});

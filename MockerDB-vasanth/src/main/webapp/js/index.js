$(document).ready(function() {
    $.ajaxSetup({
        cache: false,
        error: function(xhr) {
            if (xhr.status === 401) {
                // Redirect to the login page on 401 Unauthorized error
                window.location.href = "/MockerDB-vasanth/login";
            }
        }
    });

    var loadedTabs = {
        "mysql": false,
        "cassandra": false,
        "docs": false
    };

    $("#tabs").tabs({
        active: false,
        activate: function(event, ui) {

            var tabId = ui.newPanel.attr("id");
            var compName = tabId.toUpperCase();
            $("#pageTitle").html(compName + " - Mock Data Populator");

            if (!loadedTabs[tabId]) {
                loadTabContent(tabId);
            }
        }
    });

    loadTabContent("mysql");

    function loadTabContent(tabId) {
        console.log("Loading content for tab:", tabId);
        loadedTabs[tabId] = true;
        var contentId = tabId + "-content";

        // Use AJAX to load the content for the specified tab
        $("#" + contentId).load(tabId + ".html");
    }
    $("button").click(function () {

              $.ajax({
              type:"POST",
              url: "/MockerDB-vasanth/logout",
              success:function(response){
              console.log("logout successful");
              window.location.reload();
              }
              })

            });
});

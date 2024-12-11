var formRequestJson = {};

$(document).ready(function () {
  $("#firstForm").submit(function (event) {
    event.preventDefault();
    var databaseName = $("#database-name").val();
    var databaseHost = $("#database-host")
      .val()
      .trim()
      .replace(/[^a-zA-Z0-9:_]/g, "");
    var databaseUserName = $("#database-user-name").val();
    var databasePassword = $("#database-password").val();
    var databaseTableName = $("#database-table-name").val();

    if (databaseName === "") {
      alert("Please enter database name");
      return false;
    }
    if (databaseHost === "") {
      alert("Please enter database host");
      return false;
    }
    if (databaseUserName === "") {
      alert("Please enter database user name");
      return false;
    }
    if (databaseTableName === "") {
      alert("Please enter database table name");
      return false;
    }

    formRequestJson = {
      database_name: databaseName,
      database_host: databaseHost,
      database_username: databaseUserName,
      database_password: databasePassword,
      database_tablename: databaseTableName,
    };

    $.ajax({
      type: "POST",
      url: "app/fetchMetaData",
      dataType: "json",
      data: JSON.stringify(formRequestJson),
      success: function (data) {
        $("#errorDiv").empty();
        $("#errorDiv").removeClass("errorStyle");
        $("#errorDiv").removeClass("successStyle");
        if (data.length === 1 && data[0]["error text"]) {
          $("#tableDiv").children().remove();
          $("#errorDiv").empty();

          $("#errorDiv").append(`<h3>${data[0]["error text"]}</h3>`);
          $("#errorDiv").addClass("errorStyle");
        } else {
          $("#errorDiv").empty();
          var details = "";
          $.each(formRequestJson, function (key, value) {
            details += "<b>" + key + ":</b> " + value + ",   ";
          });
          $("#tableName").html(details);
          //$("#tableName").html(JSON.stringify(formRequestJson));
          populateMetaData(data);
        }
      },
      error: function (jqXHR, textStatus, errorThrown) {
        console.error("Error:", jqXHR.status, textStatus, errorThrown);
        if (jqXHR.status === 401) {
          window.location.reload();
        }
      },
    });
  });
});

function populateMetaData(data) {
  $.ajaxSetup({
    cache: false,
    error: function (xhr) {
      if (xhr.status === 401) {
        // Redirect to the login page on 401 Unauthorized error
        window.location.href = "/MockerDB-vasanth/login";
      }
    },
  });
  $("#tableDiv").children().remove();
  $.each(data, function (index, column) {
    var rowContainerSet = $("<div>")
      .addClass("containerSet row")
      .attr("column-name", column.column_name);

    var cell1 = $("<div>").addClass("cell");
    var label = $("<label>").text(column.column_name);
    var primaryKeyIndicator = $("<span>").text(
      column.primary_key == "true" ? " (PK)" : ""
    );
    cell1 = cell1.append(label);
    cell1 = cell1.append(primaryKeyIndicator);

    var cell2 = $("<div>").addClass("inputContainer cell");

    if (column.nullable && column.nullable == "NO") {
      cell2 = cell2.attr("required", "yes");
      primaryKeyIndicator.append($("<b>*</b>"));
    }

    if (column.type == "radio") {
      var radioButton = $(
        "<input name=" + column.column_name + " type='radio' value='true'>"
      );
      radioButton =
        column.default_value == 1
          ? radioButton.prop("checked", true)
          : radioButton;
      var radioButtonLabel = $("<label>True - 1</label>");
      cell2 = cell2.append(radioButton).append(radioButtonLabel);

      radioButton = $(
        "<input name=" + column.column_name + " type='radio' value='false'>"
      );
      radioButton =
        column.default_value == 0
          ? radioButton.prop("checked", true)
          : radioButton;
      radioButtonLabel = $("<label>False - 0</label>");
      cell2 = cell2.append(radioButton).append(radioButtonLabel);
    } else {
      input = $("<input>")
        .attr("name", column.column_name)
        .attr("type", column.type)
        .val(column.default_value);
      cell2.append(input);
    }

    /*var cell3 = $("<div>").addClass("cell");
	        var nullableSelect = $("<select>");
	        nullableSelect.append($("<option>").text("Yes").val("yes"));
	        nullableSelect.append($("<option>").text("No").val("no"));
	        nullableSelect.val(column.nullable);
	        cell3 = cell3.append(nullableSelect);*/

    cell4 = $("<div>").addClass("cell");
    var dropdown = $("<select>").addClass("possibleValues");
    dropdown.append($("<option value='range'>Range</option>"));
    dropdown.append($("<option value='radio'>Boolean Value</option>"));
    dropdown.append($("<option value='number'>Integer or Float</option>"));
    dropdown.append(
      $("<option value='text' selected='true'>Random String</option>")
    );
    dropdown.val(column.type);
    // Create the input field container
    cell4 = cell4.append(dropdown);

    var result = rowContainerSet
      .append(cell1)
      .append(cell2)
      /*.append(cell3)*/
      .append(cell4);
    //tableDiv.append(result);
    $("#tableDiv").append(result);
  });
  $("#myForm").show();
  dropdownChange();
}

function dropdownChange() {
  $(".possibleValues").change(function () {
    var selectedValue = $(this).val();
    console.log(selectedValue);
    // Clear the input container
    var parentObj = $(this).parent().parent();
    var columnName = parentObj.attr("column-name");
    var inputContainer = parentObj.find(".inputContainer");
    var isRequired =
      inputContainer.attr("required") == undefined ? false : true;
    var requiredText = isRequired ? "'required'=true" : "";
    inputContainer.empty();

    // Create the input field based on the selected value
    if (selectedValue == "range") {
      var inputField = $(
        "<input name=" + columnName + " type='text' " + requiredText + ">"
      );
      var incrementerField = $(
        "<input name=" +
          columnName +
          "-inc class='smallInput' type='number' value='1'>"
      );
      inputContainer.append(inputField).append(incrementerField);
    } else if (selectedValue == "radio") {
      var radioButton = $(
        "<input name=" + columnName + " type='radio' value='true'>"
      );
      var radioButtonLabel = $("<label>True - 1</label>");
      inputContainer.append(radioButton).append(radioButtonLabel);
      radioButton = $(
        "<input name=" + columnName + " type='radio' value='false'>"
      );
      radioButtonLabel = $("<label>False - 0</label>");
      inputContainer.append(radioButton).append(radioButtonLabel);
    } else if (selectedValue == "number") {
      var inputField = $(
        "<input name=" +
          columnName +
          " type='number' value='0' " +
          requiredText +
          ">"
      );
      inputContainer.append(inputField);
    } else if (selectedValue == "text") {
      var inputField = $(
        "<input name=" +
          columnName +
          " type='text' value='' " +
          requiredText +
          ">"
      );
      inputContainer.append(inputField);
    }
  });
}

function buildJson() {
  var values = [];
  var jsonData = {};
  $("#myForm")
    .find(".containerSet")
    .each(function () {
      //var jsonData = {};
      var containerSet = $(this);
      var columnName = $(this).attr("column-name");
      //var inputText = containerSet.find("input[type='text']").val();
      //var radioButton = containerSet.find("input[type='radio']:checked").val();
      var selectedValue = containerSet.find(".possibleValues").val();

      if (selectedValue == "range") {
        jsonData[columnName] = {
          value: containerSet.find("[name=" + columnName + "]").val(),
          incrementer: parseInt(
            containerSet.find("[name=" + columnName + "-inc]").val()
          ),
          selected_type: selectedValue,
        };
      } else if (selectedValue == "radio") {
        jsonData[columnName] = {
          value: containerSet.find("[name=" + columnName + "]:checked").val(),
          selected_type: selectedValue,
        };
      } else if (selectedValue == "number") {
        jsonData[columnName] = {
          value: parseInt(containerSet.find("[name=" + columnName + "]").val()),
          selected_type: selectedValue,
        };
      } else if (selectedValue == "text") {
        jsonData[columnName] = {
          value: containerSet.find("[name=" + columnName + "]").val(),
          selected_type: selectedValue,
        };
      }

      //values.push(jsonData);
      console.log(jsonData);
    });
  return jsonData;
  //console.log("dataa"+ JSON.stringify(jsonData));
}

$(document).ready(function () {
  $("#myForm").submit(function (event) {
    let action = document.activeElement.id;
    console.log(action);
    event.preventDefault();

    //var formData = {};
    if (action === "delete") {
      var jsonResDelete = buildJson();
      jsonResDelete["connection"] = formRequestJson;
//      console.log(jsonRes);
         $.ajax({
         type:"POST",
         url:"app/DeleteDemo",
         data:JSON.stringify(jsonResDelete),
         contentType:"application/json",
         success:function(response){
            $("#errorDiv").empty();

                   // Check if the response contains error messages

                       if (response["error text"]) {
                         $("#errorDiv").append(
                           `<h3 class='errorStyle'>${response["error text"]}</h3>`
                         );
                       }

                   $("#errorDiv").append(
                     `<h3 class='successStyle'>${response["success text"]}</h3>`
                   );
         console.log("deleted successfully");
         },
         error:function(jqXHR,textStatus,errorThrown){
         console.log(jqXHR.status);
         }

         })
    } else {
      var jsonRes = buildJson();
      jsonRes["connection"] = formRequestJson;
      $.ajax({
        type: "POST",
        url: "app/populateMockData",
        data: JSON.stringify(jsonRes),
        contentType: "application/json",
        success: function (response) {
          $("#errorDiv").empty();

          // Check if the response contains error messages

          for (let key in response) {
            if (response.hasOwnProperty(key)) {
              // Display each error message
              if (key !== "success text") {
                $("#errorDiv").append(
                  `<h3 class='errorStyle'>${response[key]}</h3>`
                );
              }
            }
          }

          $("#errorDiv").append(
            `<h3 class='successStyle'>${response["success text"]}</h3>`
          );
          console.log("Form submitted successfully!");
        },
        error: function (jqXHR, textStatus, errorThrown) {
          if (jqXHR.status === 401) {
            // window.location.href = "DynamicForm/login";
            window.location.reload();
          }

          //      console.log("error fetching dropdown data");
        },
      });
    }
  });
});

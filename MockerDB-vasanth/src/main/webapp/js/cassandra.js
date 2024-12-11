var cassFormJson = {};
var columnData = {};

$(document).ready(function () {
  $("#firstCassForm").submit(function (event) {
    event.preventDefault();
    var keySpace = $("#key_space").val();
    var hostname = $("#hostname")
      .val()
      .trim()
      .replace(/[^a-zA-Z0-9:_]/g, "");
    var tableName = $("#table_name").val();

    if (keySpace === "") {
      alert("Please enter Key Space name");
      return false;
    }
    if (hostname === "") {
      alert("Please enter cassandra hostname");
      return false;
    }
    if (tableName === "") {
      alert("Please enter cassandra table name");
      return false;
    }

    cassFormJson = {
      key_space: keySpace,
      hostname: hostname,
      table_name: tableName,
    };

    $.ajax({
      type: "POST",
      url: "app/fetchCassandraMetaData",
      dataType: "json",
      data: JSON.stringify(cassFormJson),
      success: function (data) {
        if (data[0]["error text"]) {
          $("#cassTopDiv").children().remove();
          $("#errorDiv-cass").empty();
          $("#errorDiv-cass").append(`<h3>${data[0]["error text"]}</h3>`);
          $("#errorDiv-cass").addClass("errorStyle");
        } else {
          $("#errorDiv-cass").empty();
          $("#errorDiv-cass").removeClass("errorStyle");
          var details = "";
          $.each(cassFormJson, function (key, value) {
            details += "<b>" + key + ":</b> " + value + ",   ";
          });
          $("#cassDBInfo").html(details);

          console.log(data);
          populateCassandraMetaData(data);
        }
      },
      error: function (jqXHR, textStatus, errorThrown) {
        if (jqXHR.status === 401) {
          // window.location.href = "DynamicForm/login";
          window.location.reload();
        }

        //      console.log("error fetching dropdown data");
      },
    });
  });
  //dropdownChange();
});

function populateCassandraMetaData(data) {
  $("#cassTopDiv").children().remove();

  columnData = {};
  var isPartitionKeyDone = false;
  var isClusteringKeyDone = false;
  var isOtherColumnsDone = false;
  var $tableSection;
  var $tableContainer;
  $.each(data, function (index, column) {
    //var $tableContainer = $('<div class="table-container"></div>');
    columnData[column.column_name] = column;

    var $tableRow = $('<div class="row containerSet"></div>');
    var $tableLabel = $('<div class="cell"></div>');
    var $tableInput = $('<div class="cell inputContainer"></div>');
    var $inputField = $('<input type="text" name=' + column.column_name + ">");
    var $dropDown = $('<div class="cell"></div>');
    var $selectField = $(
      '<select id="dtType" class="possibleValues">' +
        '<option value="text">Text</option>' +
        '<option value="textarea">Textarea</option>' +
        '<option value="number"">Number</option>' +
        '<option value="range">Range</option>' +
        "</select>"
    );

    if (column.is_partition_key === "true") {
      if (!isPartitionKeyDone) {
        $tableSection = $('<div class="table-section"></div>');
        $tableContainer = $('<div class="table"></div>');
        isPartitionKeyDone = true;
        $tableSection.append("<h2>Partition Keys</h2>");
      }
    } else if (column.is_clustering_key === "true") {
      if (!isClusteringKeyDone) {
        $tableSection = $('<div class="table-section"></div>');
        $tableContainer = $('<div class="table"></div>');
        isClusteringKeyDone = true;
        $tableSection.append("<h2>Clustering Keys</h2>");
      }
    } else if (!isOtherColumnsDone) {
      $tableSection = $('<div class="table-section"></div>');
      $tableContainer = $('<div class="table"></div>');
      isOtherColumnsDone = true;
      $tableSection.append("<h2>Other Columns</h2>");
    }

    $tableLabel.text(column.column_name);
    //$tableInput.addClass(column.column_name);

    $tableInput.append($inputField);
    $dropDown.append($selectField);

    if (
      column.is_partition_key === "true" &&
      column.is_clustering_key === "true"
    ) {
      //$inputField.attr('disabled', 'disabled');
      $tableLabel.append('<label class="margin-left5">(CK)</label>');
    }

    $tableRow.append($tableLabel);
    $tableRow.attr("column-name", column.column_name);

    $tableRow.append($tableInput);
    $tableRow.append($dropDown);

    $tableContainer.append($tableRow);
    $tableSection.append($tableContainer);

    //$('body').append($tableSection);
    $("#cassTopDiv").append($tableSection);
  });
  $("#cassFormSubmit").show();
  dropdownChange();
}

function dropdownChange() {
  console.log("test");
  $(".possibleValues").change(function () {
    var selectedValue = $(this).val();
    console.log(selectedValue);

    var parentObj = $(this).parent().parent();
    var columnName = parentObj.attr("column-name");
    var inputContainer = parentObj.find(".inputContainer");
    inputContainer.empty();
    if (selectedValue == "range") {
      var inputField = $("<input name=" + columnName + " type='text' >");
      var incrementerField = $(
        "<input name=" +
          columnName +
          "-inc class='smallInput' type='number' value='1'>"
      );
      inputContainer.append(inputField).append(incrementerField);
    } else if (selectedValue == "textarea") {
      var inputField = $(
        "<textarea name=" + columnName + " style='max-width:100%'>"
      );
      inputContainer.append(inputField);
    } else if (selectedValue == "text") {
      var inputField = $(
        "<input name=" + columnName + " type='text' value=''>"
      );
      inputContainer.append(inputField);
    } else if (selectedValue == "number") {
      var inputField = $(
        "<input name=" + columnName + " type='number' value='0'>"
      );
      inputContainer.append(inputField);
    }
  });
}

function buildCassJson() {
  var jsonData = {};
  $("#cassForm")
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
      } else if (selectedValue == "textarea") {
        jsonData[columnName] = {
          value: containerSet.find("[name=" + columnName + "]").val(),
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
    });
  return jsonData;
  //console.log("dataa"+ JSON.stringify(jsonData));
}

$(document).ready(function () {
  $("#cassForm").submit(function (event) {
    event.preventDefault();
    let action = document.activeElement.id;
    console.log(action);
    event.preventDefault();

    //var formData = {};
    if (action === "delete") {
      let jsonResDelete = buildCassJson();
      jsonResDelete["connection"] = cassFormJson;
      //      console.log(jsonRes);
      $.ajax({
        type: "POST",
        url: "app/deleteCassMockData",
        data: JSON.stringify(jsonResDelete),
        contentType: "application/json",
        success: function (response) {
           $("#errorDiv-cass").empty();
                   if (response["error text"]) {
                     $("#errorDiv-cass").append(
                       `<h3 class="errorStyle">${response["error text"]}</h3>`
                     );
                   } else {
                     $("#errorDiv-cass").append(
                       `<h3 class="successStyle">${response["success text"]}</h3>`
                     );
                   }
          console.log("deleted successfully");
        },
        error: function (jqXHR, textStatus, errorThrown) {
          console.log(jqXHR.status);
        },
      });
    } else {
      let jsonRes = buildCassJson();
      jsonRes["connection"] = cassFormJson;
      //var formData = {};
      console.log(jsonRes);
      $.ajax({
        type: "POST",
        url: "app/populateCassMockData",
        data: JSON.stringify(jsonRes),
        contentType: "application/json",
        success: function (data) {
          $("#errorDiv-cass").empty();
          if (data["error text"]) {
            $("#errorDiv-cass").append(
              `<h3 class="errorStyle">${data["error text"]}</h3>`
            );
          } else {
            $("#errorDiv-cass").append(
              `<h3 class="successStyle">${data["success text"]}</h3>`
            );
          }
        },
        error: function (request, error) {
          if (request.status === 401) {
            window.location.href = "/MockerDB-vasanth/login";
          }
          console.log("Failed Data ", error);
          console.log("Failed request", request);
        },
      });
    }
  });
});

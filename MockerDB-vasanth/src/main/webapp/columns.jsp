<div id="columns">
    <h2>Columns of ${tableName}</h2>
    <form id="column-values-form">
        <c:forEach var="column" items="${columns}">
            <label for="${column.columnName}">${column.columnName}</label>
            <input type="text" id="${column.columnName}" name="${column.columnName}" data-type="${column.dataType}">
            

        </c:forEach>
        <button type="submit">Submit</button>
    </form>
    <div id="populate-container"></div>
    <script>
        $(document).ready(function() {
            $("#column-values-form").submit(function(event) {
                event.preventDefault();
                var columnValues = {};
                $(":input", this).each(function() {
                    var columnName = $(this).attr("name");
                    var value = $(this).val();
                    columnValues[columnName] = value;
                });
                $.ajax({
                    type: "POST",
                    url: "PopulateTableRest",
                    data: {tableName: "${tableName}", columnValues: columnValues},
                    success: function(data) {
                        $("#populate-container").html(data);
                    }
                });
            });
        });
    </script>
</div>
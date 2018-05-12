$(".table").DataTable({
    processing: true,
    serverSide: true,
    ajax: {
        url: "/api/customer/get-table-data",
        contentType: "application/json",
        type: "POST",
        data: function (d) {
            return JSON.stringify(d);
        }
    }
});
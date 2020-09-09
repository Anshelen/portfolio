'use strict';


$(function(){
  const pageSize = 2;

  getAllUsersRequest(0, pageSize)
    .then((data) => {
      let rows = [];

      for (let i = 0; i < data.size; i++) {
        let row = $('<tr><td></td><td></td><td></td><td></td><td></td></tr>');
        row.setContent = function(contentObject) {
          row.find('td:nth-child(1)').html(contentObject.id);
          row.find('td:nth-child(2)').html(contentObject.username);
          row.find('td:nth-child(3)').html(contentObject.email);
          row.find('td:nth-child(4)').html(contentObject.enabled ? 'True' : 'False');
          row.find('td:nth-child(5)').html(contentObject.roles.join(', '));
        }
        row.clearContent = function() {
          row.find('td:nth-child(1)').html('&nbsp;');
          row.find('td:nth-child(2)').html('');
          row.find('td:nth-child(3)').html('');
          row.find('td:nth-child(4)').html('');
          row.find('td:nth-child(5)').html('');
        }

        if (i < data.numberOfElements) {
          row.setContent(data.content[i]);
        } else {
          row.clearContent();
        }
        rows.push(row);
      }

      $('#content tbody').append(rows);

      $('#pagination-container').bootpag({
        total: data.totalPages,
        leaps: true,
        firstLastUse: true,
        first: '←',
        last: '→',
        href: '#'
      }).on("page", function(event, pageNum) {
        getAllUsersRequest(pageNum - 1, pageSize)
          .then((data) => {
            for (let i = 0; i < rows.length; i++) {
              let row = rows[i];
              if (data.content.length > i) {
                row.setContent(data.content[i]);
              } else {
                row.clearContent();
              }
            }
          })
          .catch(error => {
            alert("Error when fetching data: " + error.status);
          });
      });
    })
    .catch(error => {
      alert("Error when fetching data: " + error.status);
    });
});

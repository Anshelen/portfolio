'use strict';


$().ready(function() {
  $('[data-toggle="popover"]').popover({
    html: true,
    content: function() {
      let id = $(this).data('details');
      return $('#' + id).html();
    }
  });
});

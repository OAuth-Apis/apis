$.fn.serializeObject = function()
{
  var o = {};
  var a = this.serializeArray();
  $.each(a, function() {
    if (o[this.name] !== undefined) {
      if (!o[this.name].push) {
        o[this.name] = [o[this.name]];
      }
      o[this.name].push(this.value || '');
    } else {
      o[this.name] = this.value || '';
    }
  });
  return o;
};

/*
Enhancement to $.unique, to work on non-domelements as well.
From http://paulirish.com/2010/duck-punching-with-jquery/
 */
(function($){

  var _old = $.unique;

  $.unique = function(arr){

    // do the default behavior only if we got an array of elements
    if (!!arr[0].nodeType){
      return _old.apply(this,arguments);
    } else {
      // reduce the array to contain no dupes via grep/inArray
      return $.grep(arr,function(v,k){
        return $.inArray(v,arr) === k;
      });
    }
  };
})(jQuery);
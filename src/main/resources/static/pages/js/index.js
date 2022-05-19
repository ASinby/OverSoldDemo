$(function () {

    // console.log("js文件加载成功！");
    btnAction();
});

function btnAction() {

    $("#imitatePurchase").on("click",function (e) {
        console.log("抢购开始……");

        let num = $("#purchaseNum").val();

        console.log("抢购数量：",num);

        for(let i=1; i<=num; ++i){
            let params = {
                userId: 1,  //用户ID
                productId: 1,   //商品ID
                quantity: 1 //购买数量
            };
            //通过 POST 请求后端，这 JavaScript 会采用异步请求
            $.post("./purchase", params , function (result) {
                console.log(result);
            });
        }

        console.log("抢购结束……");
    });

    $("#imitateSelect").on("click",function (e) {
        $.get("./getProductLists" , function (result) {
            console.log(result);
        });
    });
}
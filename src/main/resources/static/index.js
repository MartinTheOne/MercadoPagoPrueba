const mp=new MercadoPago('APP_USR-2724e39c-db5f-461e-a4c8-6e0e2c52b99a',{
    locale:'es-AR'
    });
    
    const MP= async ()=>{
    try{
    miArticulo={}
    miArticulo.titulo="coca"
    miArticulo.cantidad=2
    miArticulo.precio=10
    
    const response =await fetch("api/mp",{
    method:'POST',
    headers:{
    'Accept':'Application/json',
    'Content-Type': 'Application/json'
    },
    body:JSON.stringify(miArticulo)
    })
    const preference= await response.text()
    createCheckoutButton(preference)
    
    
    }
    catch(e){
    alert("error"+e)}
    }
    
    const createCheckoutButton=(preference_id)=>{
    const brickBuilder=mp.bricks();
    const generateButton= async()=>{
     if(window.checkoutButton) window.checkoutButton.unmount()
     brickBuilder.create("wallet","wallet_container",{
     initialization:{
     preferenceId:preference_id,
     },
     });
    }
    generateButton()
    }
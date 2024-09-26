const mp=new MercadoPago('APP_USR-401a2fc9-ea95-4753-9726-d89eb3e46032',{
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
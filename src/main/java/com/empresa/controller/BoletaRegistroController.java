package com.empresa.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.entidades.Boleta;
import com.empresa.entidades.Cliente;
import com.empresa.entidades.Mensaje;
import com.empresa.entidades.Producto;
import com.empresa.entidades.ProductoHasBoleta;
import com.empresa.entidades.ProductoHasBoletaPK;
import com.empresa.entidades.Seleccion;
import com.empresa.entidades.Usuario;
import com.empresa.service.BoletaService;
import com.empresa.service.ClienteService;
import com.empresa.service.ProductoService;

@RestController
@RequestMapping("/rest/boleta")
@CrossOrigin(origins = "http://localhost:4200")
public class BoletaRegistroController {

	
	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ProductoService productoService;

	@Autowired
	private BoletaService boletaService;

	
	@RequestMapping("/verBoletaRegistro")
	public String verBoleta() {
		return "boletaRegistro";
	}  
	
	@GetMapping("/cargaCliente/{filtro}")
	@ResponseBody
	public ResponseEntity<List<Cliente>>  listaCliente(@PathVariable("filtro") String filtro){
		int page = 0;
		int size = 5; //Muestre los primer cinco elementos
		Pageable pageable = PageRequest.of(page, size);
		
		List<Cliente> lista = clienteService.listaCliente(filtro+"%", pageable);
		return ResponseEntity.ok(lista);
	}
	
	@GetMapping("/cargaProducto/{filtro}")
	@ResponseBody()
	public ResponseEntity<List<Producto>>  listaProducto(@PathVariable("filtro")String filtro){
		int page = 0;
		int size = 5; //Muestre los primer cinco elementos
		Pageable pageable = PageRequest.of(page, size);
		
		List<Producto> lista = productoService.listaproducto(filtro+"%", pageable);
		return ResponseEntity.ok(lista);
	}
	
	@RequestMapping("/registraBoleta")
	@ResponseBody()
	public ResponseEntity<Mensaje> registra(@RequestBody Cliente cliente , @RequestBody List<Seleccion> seleccionados){
		Mensaje   objMensaje = new Mensaje();
		
		List<ProductoHasBoleta> detalles = new ArrayList<ProductoHasBoleta>();
		for (Seleccion seleccion : seleccionados) {
			
			ProductoHasBoletaPK pk = new ProductoHasBoletaPK();
			pk.setIdProducto(seleccion.getIdProducto());
			
			ProductoHasBoleta psb = new ProductoHasBoleta();
			psb.setPrecio(seleccion.getPrecio());
			psb.setCantidad(seleccion.getCantidad());
			psb.setProductoHasBoletaPK(pk);
			
			detalles.add(psb);
		}
		
		
		Usuario objUsuario = new Usuario();
		objUsuario.setIdUsuario(1);
		
		Boleta obj = new Boleta();
		obj.setCliente(cliente);
		obj.setDetallesBoleta(detalles);
		obj.setUsuario(objUsuario);
		
		Boleta objBoleta =  boletaService.insertaBoleta(obj);
		
		String salida = "-1";
		
		if (objBoleta != null) {
				salida = "Se generó la boleta con código N° : " + objBoleta.getIdBoleta() + "<br><br>";
				salida += "Cliente: " + objBoleta.getCliente().getNombre() + "<br><br>";
				salida += "<table class=\"table\"><tr><td>Producto</td><td>Precio</td><td>Cantidad</td><td>Subtotal</td></tr>";
				double monto = 0;
				for (Seleccion x : seleccionados) {
					salida += "<tr><td>"  + x.getNombre() 
							+ "</td><td>" + x.getPrecio() 
							+ "</td><td>" + x.getCantidad()
							+ "</td><td>" + x.getTotalParcial() + "</td></tr>";
					monto += x.getCantidad() * x.getPrecio();
				}
				salida += "</table><br>";
				salida += "Monto a pagar : " + monto;

				seleccionados.clear();
				objMensaje.setTexto(salida);	
		}
		
		return ResponseEntity.ok(objMensaje);
	}
	
}

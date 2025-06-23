package com.perfulandia.perfulandia_producto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfulandia.perfulandia_producto.model.Producto;
import com.perfulandia.perfulandia_producto.model.ProductoModel;
import com.perfulandia.perfulandia_producto.service.ProductoHateoasService;
import com.perfulandia.perfulandia_producto.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private ProductoHateoasService productoHateoasService;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto producto1;
    private Producto producto2;
    private ProductoModel productoModel1;

    @BeforeEach
    void setUp() {
        producto1 = new Producto(1, "Perfume A", 100, 50.0);
        producto2 = new Producto(2, "Perfume B", 50, 75.0);
        productoModel1 = new ProductoModel(producto1);

        Link selfLink = linkTo(methodOn(ProductoController.class).buscarProducto(producto1.getId())).withSelfRel();
        productoModel1.add(selfLink);
    }

    @Test
    void testListarProductos() throws Exception {
        List<Producto> productos = Arrays.asList(producto1, producto2);
        List<ProductoModel> productoModels = Arrays.asList(productoModel1, new ProductoModel(producto2));
        CollectionModel<ProductoModel> collectionModel = CollectionModel.of(productoModels,
                linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel());

        given(productoService.listarProductos()).willReturn(productos);
        given(productoHateoasService.toModel(any(Producto.class))).willReturn(new ProductoModel(new Producto()));
        given(productoHateoasService.toCollectionModel(any())).willReturn(collectionModel);


        mockMvc.perform(get("/api/v1/producto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    void testCrearProducto() throws Exception {
        given(productoService.crearProducto(any(Producto.class))).willReturn(producto1);
        given(productoHateoasService.toModel(producto1)).willReturn(productoModel1);

        mockMvc.perform(post("/api/v1/producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto1)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(producto1.getId()))
                .andExpect(jsonPath("$.nombre").value(producto1.getNombre()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testBuscarProducto() throws Exception {
        given(productoService.buscarProducto(1)).willReturn(producto1);
        given(productoHateoasService.toModel(producto1)).willReturn(productoModel1);

        mockMvc.perform(get("/api/v1/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producto1.getId()))
                .andExpect(jsonPath("$.nombre").value(producto1.getNombre()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testActualizarProducto() throws Exception {
        given(productoService.actualizarProducto(anyInt(), any(Producto.class))).willReturn(producto1);
        given(productoHateoasService.toModel(producto1)).willReturn(productoModel1);

        mockMvc.perform(put("/api/v1/producto/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producto1.getId()))
                .andExpect(jsonPath("$.nombre").value(producto1.getNombre()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testActualizarParcialProducto() throws Exception {
        Producto partialUpdate = new Producto();
        partialUpdate.setNombre("Nuevo Nombre");

        given(productoService.buscarProducto(1)).willReturn(producto1);
        given(productoService.actualizarProducto(anyInt(), any(Producto.class))).willReturn(producto1);
        given(productoHateoasService.toModel(producto1)).willReturn(productoModel1);


        mockMvc.perform(patch("/api/v1/producto/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(producto1.getId()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testEliminarProducto() throws Exception {
        doNothing().when(productoService).eliminarProducto(1);

        mockMvc.perform(delete("/api/v1/producto/1"))
                .andExpect(status().isNoContent());
    }
} 
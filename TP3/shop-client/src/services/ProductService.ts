import axios, { AxiosResponse } from 'axios';
import { MinimalProduct, Product, ResponseArray } from '../types';

export function getProducts(page: number, size: number): Promise<ResponseArray<Product>> {
    return axios.get(`${import.meta.env.REACT_APP_API}/products?page=${page}&size=${size}`);
}

export function getProductsbyShop(shopId: string, page: number, size: number): Promise<ResponseArray<Product>> {
    return axios.get(`${import.meta.env.REACT_APP_API}/products?shopId=${shopId}&page=${page}&size=${size}`);
}

export function getProductsbyShopAndCategory(
    shopId: string,
    categoryId: number,
    page: number,
    size: number,
): Promise<ResponseArray<Product>> {
    return axios.get(
        `${import.meta.env.REACT_APP_API}/products?shopId=${shopId}&categoryId=${categoryId}&page=${page}&size=${size}`,
    );
}

export function getProduct(id: string): Promise<AxiosResponse<Product>> {
    return axios.get(`${import.meta.env.REACT_APP_API}/products/${id}`);
}

export function createProduct(product: MinimalProduct): Promise<AxiosResponse<Product>> {
    console.log("product envoyé service ", product)
    return axios.post(`${import.meta.env.REACT_APP_API}/products`, product);
}

export function editProduct(product: MinimalProduct): Promise<AxiosResponse<Product>> {
    return axios.put(`${import.meta.env.REACT_APP_API}/products`, product);
}

export function deleteProduct(id: string): Promise<AxiosResponse<Product>> {
    return axios.delete(`${import.meta.env.REACT_APP_API}/products/${id}`);
}

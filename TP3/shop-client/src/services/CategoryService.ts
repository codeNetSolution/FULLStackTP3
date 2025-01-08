import axios, { AxiosResponse } from 'axios';
import { Category, MinimalCategory, ResponseArray } from '../types';

export function getCategories(page: number, size: number): Promise<ResponseArray<Category>> {
    return axios.get(`${import.meta.env.REACT_APP_API}/categories?page=${page}&size=${size}`);
}

export function getCategory(id: string): Promise<AxiosResponse<Category>> {
    return axios.get(`${import.meta.env.REACT_APP_API}/categories/${id}`);
}

export function createCategory(category: MinimalCategory): Promise<AxiosResponse<Category>> {
    return axios.post(`${import.meta.env.REACT_APP_API}/categories`, category);
}

export function editCategory(category: MinimalCategory): Promise<AxiosResponse<Category>> {
    return axios.put(`${import.meta.env.REACT_APP_API}/categories`, category);
}

export function deleteCategory(id: string): Promise<AxiosResponse<Category>> {
    return axios.delete(`${import.meta.env.REACT_APP_API}/categories/${id}`);
}

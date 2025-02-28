import OpeningHours from './openingHours';

export type Shop = {
    id: number;
    createdAt: Date;
    name: string;
    inVacations: boolean;
    openingHours: OpeningHours[];
    nbProducts: number;
};

export type MinimalShop = {
    id?: string;
    name: string;
    inVacations: boolean;
    openingHours: { day: number; openAt: string; closeAt: string }[];
};

export interface ShopSearch {
    id: number;
    name: string;
    inVacations: boolean;
    createdAt: string;
    nbProducts: number;
  }
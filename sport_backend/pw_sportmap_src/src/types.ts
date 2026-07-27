export interface Venue {
  id: string;
  name: string;
  sport: string;
  district: string;
  address: string;
  rating: number;
  reviewsCount: number;
  image: string;
  type: 'cancha' | 'ruta';
  tags: string[];
  featured?: boolean;
}

export interface StoreItem {
  id: string;
  name: string;
  category: 'Calzado' | 'Balones' | 'Hidratación' | 'Accesorios' | 'Ropa';
  sport: string;
  rating: number;
  image: string;
  inStock: boolean;
  featured?: boolean;
}

export interface SportCategory {
  id: string;
  name: string;
  iconName?: string;
  image: string;
  count: string;
  description: string;
}

export interface FAQItem {
  question: string;
  answer: string;
  category: string;
}

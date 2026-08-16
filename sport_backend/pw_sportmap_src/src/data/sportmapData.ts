import { Venue, StoreItem, SportCategory, FAQItem } from '../types';

export const SPORTS_CATEGORIES: SportCategory[] = [
  {
    id: 'futbol',
    name: 'Fútbol & Futsal',
    iconName: 'Trophy',
    image: 'https://images.unsplash.com/photo-1529900748604-07564a03e7a6?auto=format&fit=crop&q=80&w=500',
    count: '48 Canchas',
    description: 'Canchas sintéticas y de gras natural con luz LED'
  },
  {
    id: 'tenis',
    name: 'Tenis & Pádel',
    iconName: 'CircleDot',
    image: 'https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?auto=format&fit=crop&q=80&w=500',
    count: '24 Courts',
    description: 'Canchas de arcilla, cemento y cristal de pádel'
  },
  {
    id: 'voley',
    name: 'Vóley & Playa',
    iconName: 'Volleyball',
    image: 'https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?auto=format&fit=crop&q=80&w=500',
    count: '18 Coliseos',
    description: 'Losa deportiva y arenas preparadas'
  },
  {
    id: 'natacion',
    name: 'Natación',
    iconName: 'Waves',
    image: 'https://images.unsplash.com/photo-1530549387789-4c1017266635?auto=format&fit=crop&q=80&w=500',
    count: '12 Piscinas',
    description: 'Piscinas temperadas y piletas olímpicas'
  },
  {
    id: 'running',
    name: 'Running',
    iconName: 'Activity',
    image: 'https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?auto=format&fit=crop&q=80&w=500',
    count: '15 Rutas',
    description: 'Circuitos urbanos, parques y malecones'
  },
  {
    id: 'ciclismo',
    name: 'Ciclismo',
    iconName: 'Bike',
    image: 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&q=80&w=500',
    count: '10 Ciclovías',
    description: 'Rutas seguras y trazados costeros'
  },
  {
    id: 'bienestar',
    name: 'Bienestar & Gym',
    iconName: 'Dumbbell',
    image: 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&q=80&w=500',
    count: '20 Centros',
    description: 'Zonas functional, yoga al aire libre y calistenia'
  }
];

export const LIMA_DISTRICTS = [
  'Todos los distritos',
  'San Borja',
  'San Isidro',
  'Miraflores',
  'Surco',
  'La Victoria',
  'Cercado de Lima',
  'Barranco',
  'Costa Verde'
];

export const VENUES_DATA: Venue[] = [
  {
    id: 'v1',
    name: 'Cancha Fútbol San Borja Central',
    sport: 'Fútbol & Futsal',
    district: 'San Borja',
    address: 'Av. Paseo de la Breña s/n, cerca al Pentagonito',
    rating: 4.9,
    reviewsCount: 142,
    image: 'https://images.unsplash.com/photo-1529900748604-07564a03e7a6?auto=format&fit=crop&q=80&w=800',
    type: 'cancha',
    tags: ['Gras Sintético', 'Luz LED Nocturna', 'Estacionamiento', 'Vestuarios'],
    featured: true
  },
  {
    id: 'v2',
    name: 'Club Tenis San Borja',
    sport: 'Tenis & Pádel',
    district: 'San Borja',
    address: 'Av. Angamos Este 2450',
    rating: 4.8,
    reviewsCount: 98,
    image: 'https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?auto=format&fit=crop&q=80&w=800',
    type: 'cancha',
    tags: ['Cancha Arcilla', 'Iluminación', 'Cafetería', 'Profesor Disponible'],
    featured: true
  },
  {
    id: 'v3',
    name: 'Cancha Básquetbol San Isidro',
    sport: 'Básquetbol',
    district: 'San Isidro',
    address: 'Av. Augusto Pérez Araníbar 1590',
    rating: 4.9,
    reviewsCount: 115,
    image: 'https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&q=80&w=800',
    type: 'cancha',
    tags: ['Losa Techada', 'Aros Regulables', 'Tableros de Vidrio', 'Vestuarios'],
    featured: true
  },
  {
    id: 'v4',
    name: 'Coliseo Vóley Cercado de Lima',
    sport: 'Vóley & Playa',
    district: 'Cercado de Lima',
    address: 'Jr. Washington 1200, Cercado',
    rating: 4.8,
    reviewsCount: 110,
    image: 'https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?auto=format&fit=crop&q=80&w=800',
    type: 'cancha',
    tags: ['Piso Flotante', 'Redes Oficiales', 'Tribunas', 'Luz LED'],
    featured: true
  },
  {
    id: 'v5',
    name: 'Centro Acuático Natación Miraflores',
    sport: 'Natación',
    district: 'Miraflores',
    address: 'Av. del Ejército 1300',
    rating: 4.9,
    reviewsCount: 135,
    image: 'https://images.unsplash.com/photo-1530549387789-4c1017266635?auto=format&fit=crop&q=80&w=800',
    type: 'cancha',
    tags: ['Piscina Temperada', 'Carriles Plegables', 'Duchas Agua Caliente'],
    featured: true
  },
  {
    id: 'v6',
    name: 'Pádel & Tenis Club Costa Verde',
    sport: 'Tenis & Pádel',
    district: 'Miraflores',
    address: 'Malecón de la Reserva, Miraflores',
    rating: 4.9,
    reviewsCount: 180,
    image: 'https://images.unsplash.com/photo-1554068865-24cecd4e34b8?auto=format&fit=crop&q=80&w=800',
    type: 'cancha',
    tags: ['Canchas Panorámicas', 'Vista al Mar', 'Luz LED Nocturna', 'Vestuarios'],
    featured: true
  },
  {
    id: 'v7',
    name: 'Jockey Club del Perú (Sede Surco)',
    sport: 'Fútbol & Futsal',
    district: 'Surco',
    address: 'Av. Javier Prado Este 4200, Surco',
    rating: 4.9,
    reviewsCount: 230,
    image: 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&q=80&w=800',
    type: 'cancha',
    tags: ['Gras Natural', 'Tribunas', 'Seguridad 24/7', 'Vestuarios VIP'],
    featured: false
  },
  {
    id: 'v8',
    name: 'Cancha Multiuso Parque Reducto',
    sport: 'Básquetbol',
    district: 'Miraflores',
    address: 'Calle Ramón Ribeyro 201, Miraflores',
    rating: 4.8,
    reviewsCount: 160,
    image: 'https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&q=80&w=800',
    type: 'cancha',
    tags: ['Losa Techada', 'Iluminación Nocturna', 'Aros Regulables', 'Vestuarios'],
    featured: false
  }
];

export const STORE_ITEMS: StoreItem[] = [
  {
    id: 's1',
    name: 'Chimpunes Pro Sintético Peru Edition',
    category: 'Calzado',
    sport: 'Fútbol',
    rating: 4.9,
    image: 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55?auto=format&fit=crop&q=80&w=800',
    inStock: true,
    featured: true
  },
  {
    id: 's2',
    name: 'Balón Oficial de Fútbol N° 5 Termosellado',
    category: 'Balones',
    sport: 'Fútbol',
    rating: 4.8,
    image: 'https://images.unsplash.com/photo-1614632537190-23e4146777db?auto=format&fit=crop&q=80&w=800',
    inStock: true,
    featured: true
  },
  {
    id: 's3',
    name: 'Tomatodo Térmico Acero 1 Litro SportMap',
    category: 'Hidratación',
    sport: 'Multideporte',
    rating: 4.9,
    image: 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&q=80&w=800',
    inStock: true,
    featured: false
  },
  {
    id: 's4',
    name: 'Zapatillas Tenis Pro Cushion',
    category: 'Calzado',
    sport: 'Tenis',
    rating: 4.9,
    image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&q=80&w=800',
    inStock: true,
    featured: true
  },
  {
    id: 's5',
    name: 'Camiseta Transpirable DryFit SportMap',
    category: 'Ropa',
    sport: 'Multideporte',
    rating: 4.7,
    image: 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?auto=format&fit=crop&q=80&w=800',
    inStock: true,
    featured: false
  },
  {
    id: 's6',
    name: 'Raqueta Tenis Pro Carbon Edition',
    category: 'Accesorios',
    sport: 'Tenis',
    rating: 4.8,
    image: 'https://images.unsplash.com/photo-1622279457486-62dcc4a431d6?auto=format&fit=crop&q=80&w=800',
    inStock: true,
    featured: true
  }
];

export const FAQ_DATA: FAQItem[] = [
  {
    question: '¿La app SportMap es gratuita para descargar y usar?',
    answer: 'Sí, SportMap es totalmente gratuita para explorar canchas, consultar rutas, ver disponibilidad y acceder al catálogo. Solo pagas cuando decides realizar una reserva de cancha o comprar algún artículo deportivo.',
    category: 'General'
  },
  {
    question: '¿Qué métodos de pago acepta SportMap?',
    answer: 'Por ahora aceptamos únicamente Yape y Plin para confirmar tu reserva al instante de forma fácil y segura. Próximamente agregaremos más medios de pago.',
    category: 'Pagos'
  },
  {
    question: '¿En qué distritos de Lima opera SportMap?',
    answer: 'SportMap cubre todo Lima Metropolitana. Actualmente tenemos alta concentración de canchas y rutas verificadas en San Borja, San Isidro, Miraflores, Surco, La Victoria, Cercado de Lima y Barranco, y estamos sumando nuevas canchas cada semana.',
    category: 'Cobertura'
  },
  {
    question: '¿Cómo funcionan las Medallas y el Plan Premium?',
    answer: 'Cada vez que reservas o completas una ruta deportiva, ganas Puntos SportMap y desbloqueas medallas. El Plan Premium te otorga reservas prioritarias en horas pico, cashback en la tienda y promociones exclusivas con marcas aliadas.',
    category: 'Beneficios'
  },
  {
    question: 'Tengo una cancha o tienda deportiva, ¿cómo puedo unirme?',
    answer: '¡Es muy sencillo! Puedes ponerte en contacto a través de nuestro formulario en la sección de Contacto o escribirnos a aliados@sportmap.pe para dar de alta tu complejo en la plataforma.',
    category: 'Aliados'
  }
];

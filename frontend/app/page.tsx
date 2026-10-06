"use client";

import Image from "next/image";
import { FormEvent, useEffect, useMemo, useState } from "react";
import {
  BadgeCheck,
  Bike,
  CreditCard,
  Filter,
  MapPin,
  Minus,
  Plus,
  Search,
  ShoppingBag,
  Sparkles,
  Timer,
  UserRound
} from "lucide-react";

const API = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8081/api/v1";

type Store = {
  id: number;
  name: string;
  area: string;
  open: boolean;
  etaMinutes: number;
};

type Product = {
  id: number;
  store: Store;
  name: string;
  category: string;
  imageUrl: string;
  price: number;
  stockQuantity: number;
  available: boolean;
};

type CartItem = {
  product: Product;
  quantity: number;
  lineTotal: number;
};

type Cart = {
  userId: number;
  items: CartItem[];
  subtotal: number;
  deliveryFee: number;
  total: number;
};

type User = {
  id: number;
  email: string;
  displayName: string;
  mfaEnabled: boolean;
};

type Order = {
  id: number;
  status: string;
  paymentStatus: string;
  paymentMethod: string;
  store: Store;
  deliveryAddress: string;
  subtotal: number;
  deliveryFee: number;
  total: number;
  etaMinutes: number;
  placedAt: string;
  items: Array<{
    name: string;
    quantity: number;
    unitPrice: number;
    lineTotal: number;
  }>;
};

const fallbackCart: Cart = {
  userId: 0,
  items: [],
  subtotal: 0,
  deliveryFee: 0,
  total: 0
};

async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {})
    }
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed: ${response.status}`);
  }

  return response.json() as Promise<T>;
}

function money(value: number) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0
  }).format(value);
}

export default function Home() {
  const [mode, setMode] = useState<"login" | "register">("register");
  const [user, setUser] = useState<User | null>(null);
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<string[]>([]);
  const [cart, setCart] = useState<Cart>(fallbackCart);
  const [orders, setOrders] = useState<Order[]>([]);
  const [selectedCategory, setSelectedCategory] = useState("All");
  const [query, setQuery] = useState("");
  const [address, setAddress] = useState("221, Lake View Road, Velachery, Chennai");
  const [cardName, setCardName] = useState("Swastik Nandy");
  const [cardNumber, setCardNumber] = useState("4242 4242 4242 4242");
  const [message, setMessage] = useState("Create an account to start ordering.");
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    void loadCatalog();
  }, []);

  async function loadCatalog() {
    const [nextProducts, nextCategories] = await Promise.all([
      api<Product[]>("/products"),
      api<string[]>("/categories")
    ]);
    setProducts(nextProducts);
    setCategories(nextCategories);
  }

  async function loadCart(userId: number) {
    setCart(await api<Cart>(`/users/${userId}/cart`));
  }

  async function loadOrders(userId: number) {
    setOrders(await api<Order[]>(`/users/${userId}/orders`));
  }

  async function submitAuth(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setBusy(true);
    const data = new FormData(event.currentTarget);
    const body =
      mode === "register"
        ? {
            email: String(data.get("email")),
            displayName: String(data.get("displayName")),
            password: String(data.get("password"))
          }
        : {
            email: String(data.get("email")),
            password: String(data.get("password"))
          };

    try {
      const result = await api<{ token: string; user: User }>(
        mode === "register" ? "/auth/register" : "/auth/login",
        {
          method: "POST",
          body: JSON.stringify(body)
        }
      );
      setUser(result.user);
      setMessage(`Signed in as ${result.user.displayName}. MFA is ready for future integration.`);
      await Promise.all([loadCart(result.user.id), loadOrders(result.user.id)]);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Auth failed");
    } finally {
      setBusy(false);
    }
  }

  async function add(product: Product) {
    if (!user) {
      setMessage("Login first, then add items.");
      return;
    }
    const existing = cart.items.find((item) => item.product.id === product.id);
    const quantity = (existing?.quantity ?? 0) + 1;
    const nextCart = await api<Cart>(`/users/${user.id}/cart/items`, {
      method: "PUT",
      body: JSON.stringify({ productId: product.id, quantity })
    });
    setCart(nextCart);
    setMessage(`${product.name} added to cart.`);
  }

  async function remove(product: Product) {
    if (!user) return;
    const existing = cart.items.find((item) => item.product.id === product.id);
    if (!existing) return;
    if (existing.quantity === 1) {
      setCart(
        await api<Cart>(`/users/${user.id}/cart/items/${product.id}`, {
          method: "DELETE"
        })
      );
    } else {
      setCart(
        await api<Cart>(`/users/${user.id}/cart/items`, {
          method: "PUT",
          body: JSON.stringify({ productId: product.id, quantity: existing.quantity - 1 })
        })
      );
    }
  }

  async function checkout() {
    if (!user) return;
    if (!cart.items.length) {
      setMessage("Cart is empty.");
      return;
    }
    setBusy(true);
    try {
      const order = await api<Order>("/checkout", {
        method: "POST",
        body: JSON.stringify({
          userId: user.id,
          deliveryAddress: address,
          paymentMethod: `CARD ${cardNumber.slice(-4)}`
        })
      });
      setMessage(`Payment confirmed. Order #${order.id} is being packed.`);
      setCart(fallbackCart);
      await Promise.all([loadOrders(user.id), loadCatalog()]);
    } finally {
      setBusy(false);
    }
  }

  const filtered = useMemo(() => {
    return products.filter((product) => {
      const categoryMatch = selectedCategory === "All" || product.category === selectedCategory;
      const queryMatch = product.name.toLowerCase().includes(query.toLowerCase());
      return categoryMatch && queryMatch;
    });
  }, [products, query, selectedCategory]);

  const stockCount = products.reduce((sum, product) => sum + product.stockQuantity, 0);

  return (
    <main className="min-h-screen bg-[#f7f8f3] text-neutral-950">
      <header className="sticky top-0 z-20 border-b border-lime-900/10 bg-white/95 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center gap-4 px-4 py-3">
          <div className="flex h-11 w-11 items-center justify-center rounded-md bg-lime-500 text-xl font-black text-neutral-950">
            QC
          </div>
          <div className="min-w-0">
            <h1 className="text-xl font-black tracking-normal">QuickCart</h1>
            <p className="flex items-center gap-1 text-sm text-neutral-600">
              <MapPin size={15} /> Chennai delivery in 5 minutes
            </p>
          </div>
          <div className="ml-auto hidden items-center gap-2 rounded-md bg-neutral-100 px-3 py-2 text-sm font-semibold md:flex">
            <Timer size={16} /> {stockCount} items available now
          </div>
          <div className="rounded-md bg-neutral-950 px-3 py-2 text-sm font-bold text-white">
            {cart.items.length} cart items
          </div>
        </div>
      </header>

      <section className="mx-auto grid max-w-7xl gap-5 px-4 py-5 lg:grid-cols-[280px_1fr_360px]">
        <aside className="space-y-4">
          <section className="rounded-md border border-neutral-200 bg-white p-4 shadow-sm">
            <div className="mb-3 flex items-center gap-2 font-black">
              <UserRound size={18} /> Account
            </div>
            <form className="space-y-3" onSubmit={submitAuth}>
              <div className="grid grid-cols-2 rounded-md bg-neutral-100 p-1 text-sm font-bold">
                <button
                  type="button"
                  className={`rounded px-3 py-2 ${mode === "register" ? "bg-white shadow-sm" : ""}`}
                  onClick={() => setMode("register")}
                >
                  Register
                </button>
                <button
                  type="button"
                  className={`rounded px-3 py-2 ${mode === "login" ? "bg-white shadow-sm" : ""}`}
                  onClick={() => setMode("login")}
                >
                  Login
                </button>
              </div>
              <input
                name="email"
                required
                type="email"
                defaultValue="swastik@example.com"
                className="w-full rounded-md border border-neutral-200 px-3 py-2"
                placeholder="Email"
              />
              {mode === "register" && (
                <input
                  name="displayName"
                  required
                  defaultValue="Swastik"
                  className="w-full rounded-md border border-neutral-200 px-3 py-2"
                  placeholder="Display name"
                />
              )}
              <input
                name="password"
                required
                type="password"
                defaultValue="password123"
                className="w-full rounded-md border border-neutral-200 px-3 py-2"
                placeholder="Password"
              />
              <button
                disabled={busy}
                className="w-full rounded-md bg-neutral-950 px-3 py-2 font-black text-white disabled:opacity-60"
              >
                {mode === "register" ? "Create account" : "Login"}
              </button>
            </form>
            <p className="mt-3 rounded-md bg-lime-50 p-3 text-sm text-neutral-700">{message}</p>
          </section>

          <section className="rounded-md border border-neutral-200 bg-white p-4 shadow-sm">
            <div className="mb-3 flex items-center gap-2 font-black">
              <Filter size={18} /> Categories
            </div>
            <div className="grid gap-2">
              {["All", ...categories].map((category) => (
                <button
                  key={category}
                  onClick={() => setSelectedCategory(category)}
                  className={`rounded-md px-3 py-2 text-left text-sm font-bold ${
                    selectedCategory === category ? "bg-lime-400 text-neutral-950" : "bg-neutral-100"
                  }`}
                >
                  {category}
                </button>
              ))}
            </div>
          </section>
        </aside>

        <section className="space-y-4">
          <div className="rounded-md bg-neutral-950 px-5 py-5 text-white">
            <div className="flex flex-wrap items-center gap-3">
              <Sparkles className="text-lime-300" />
              <div>
                <h2 className="text-2xl font-black">Blinkit-style demo store</h2>
                <p className="text-sm text-neutral-300">
                  Search groceries, filter availability, pay by card, and jump straight to confirmation.
                </p>
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2 rounded-md border border-neutral-200 bg-white px-3 py-2 shadow-sm">
            <Search size={18} className="text-neutral-500" />
            <input
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              className="w-full outline-none"
              placeholder="Search milk, bread, coffee..."
            />
          </div>

          <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
            {filtered.map((product) => {
              const quantity = cart.items.find((item) => item.product.id === product.id)?.quantity ?? 0;
              return (
                <article key={product.id} className="rounded-md border border-neutral-200 bg-white p-3 shadow-sm">
                  <div className="relative mb-3 aspect-[4/3] overflow-hidden rounded-md bg-neutral-100">
                    <Image src={product.imageUrl} alt={product.name} fill className="object-cover" sizes="300px" />
                    <div className="absolute left-2 top-2 rounded bg-white px-2 py-1 text-xs font-black">
                      {product.stockQuantity > 0 ? `${product.stockQuantity} left` : "Out"}
                    </div>
                  </div>
                  <div className="min-h-24">
                    <p className="text-xs font-black uppercase text-lime-700">{product.category}</p>
                    <h3 className="mt-1 text-base font-black">{product.name}</h3>
                    <p className="mt-1 flex items-center gap-1 text-sm text-neutral-600">
                      <Bike size={15} /> {product.store.etaMinutes} min from {product.store.area}
                    </p>
                  </div>
                  <div className="mt-3 flex items-center justify-between">
                    <span className="text-lg font-black">{money(product.price)}</span>
                    {quantity ? (
                      <div className="flex items-center gap-2 rounded-md bg-lime-400 p-1 font-black">
                        <button onClick={() => remove(product)} className="rounded bg-white p-1">
                          <Minus size={16} />
                        </button>
                        <span className="w-6 text-center">{quantity}</span>
                        <button onClick={() => add(product)} className="rounded bg-white p-1">
                          <Plus size={16} />
                        </button>
                      </div>
                    ) : (
                      <button
                        onClick={() => add(product)}
                        disabled={!product.available}
                        className="rounded-md bg-lime-400 px-3 py-2 text-sm font-black disabled:bg-neutral-200"
                      >
                        Add
                      </button>
                    )}
                  </div>
                </article>
              );
            })}
          </div>
        </section>

        <aside className="space-y-4">
          <section className="rounded-md border border-neutral-200 bg-white p-4 shadow-sm">
            <div className="mb-3 flex items-center gap-2 font-black">
              <ShoppingBag size={18} /> Cart
            </div>
            <div className="space-y-3">
              {cart.items.length === 0 && <p className="text-sm text-neutral-500">Your cart is empty.</p>}
              {cart.items.map((item) => (
                <div key={item.product.id} className="flex items-center justify-between gap-3 border-b border-neutral-100 pb-3">
                  <div>
                    <p className="font-bold">{item.product.name}</p>
                    <p className="text-sm text-neutral-500">
                      {item.quantity} x {money(item.product.price)}
                    </p>
                  </div>
                  <p className="font-black">{money(item.lineTotal)}</p>
                </div>
              ))}
            </div>
            <div className="mt-4 space-y-2 text-sm">
              <div className="flex justify-between">
                <span>Subtotal</span>
                <strong>{money(cart.subtotal)}</strong>
              </div>
              <div className="flex justify-between">
                <span>Delivery</span>
                <strong>{money(cart.deliveryFee)}</strong>
              </div>
              <div className="flex justify-between border-t border-neutral-200 pt-2 text-lg">
                <span className="font-black">Total</span>
                <strong>{money(cart.total)}</strong>
              </div>
            </div>
          </section>

          <section className="rounded-md border border-neutral-200 bg-white p-4 shadow-sm">
            <div className="mb-3 flex items-center gap-2 font-black">
              <CreditCard size={18} /> Checkout
            </div>
            <textarea
              value={address}
              onChange={(event) => setAddress(event.target.value)}
              className="mb-2 min-h-20 w-full rounded-md border border-neutral-200 px-3 py-2"
            />
            <input
              value={cardName}
              onChange={(event) => setCardName(event.target.value)}
              className="mb-2 w-full rounded-md border border-neutral-200 px-3 py-2"
            />
            <input
              value={cardNumber}
              onChange={(event) => setCardNumber(event.target.value)}
              className="mb-3 w-full rounded-md border border-neutral-200 px-3 py-2"
            />
            <button
              onClick={checkout}
              disabled={busy || !cart.items.length}
              className="flex w-full items-center justify-center gap-2 rounded-md bg-lime-400 px-3 py-3 font-black text-neutral-950 disabled:bg-neutral-200"
            >
              <BadgeCheck size={18} /> Pay and confirm
            </button>
          </section>

          <section className="rounded-md border border-neutral-200 bg-white p-4 shadow-sm">
            <div className="mb-3 flex items-center gap-2 font-black">
              <BadgeCheck size={18} /> Orders
            </div>
            <div className="space-y-3">
              {orders.length === 0 && <p className="text-sm text-neutral-500">Confirmed orders will appear here.</p>}
              {orders.map((order) => (
                <div key={order.id} className="rounded-md bg-neutral-100 p-3">
                  <div className="flex items-center justify-between">
                    <strong>Order #{order.id}</strong>
                    <span className="rounded bg-lime-400 px-2 py-1 text-xs font-black">{order.paymentStatus}</span>
                  </div>
                  <p className="mt-1 text-sm text-neutral-600">
                    {order.status.replaceAll("_", " ")} · ETA {order.etaMinutes} min
                  </p>
                  <p className="mt-2 font-black">{money(order.total)}</p>
                </div>
              ))}
            </div>
          </section>
        </aside>
      </section>
    </main>
  );
}

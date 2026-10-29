import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { PageHeader, ErrorBanner, Spinner, EmptyState } from "../../components/ui/Feedback";
import { Input, Select } from "../../components/ui/Field";
import { Modal } from "../../components/ui/Modal";
import { Button } from "../../components/ui/Button";
import { ClassCard } from "../../components/member/ClassCard";
import { useClassCatalog } from "../../hooks/useClassCatalog";
import { useAction } from "../../hooks/useAction";
import { bookClass, fetchMyBookings } from "../../Redux/booking/bookingSlice";
import { fetchBalance } from "../../Redux/credit/creditSlice";
import { CATEGORIES, addDaysIso, BOOKING_WINDOW_DAYS, longDate, shortTime, todayIso } from "../../utils/format";

export default function Explore() {
  const dispatch = useDispatch();
  const [params] = useSearchParams();
  const { items, loading, error } = useClassCatalog();
  const mine = useSelector((s) => s.booking.mine);
  const { run, busy } = useAction();
  const [category, setCategory] = useState(params.get("category") || "");
  const [city, setCity] = useState("");
  const [query, setQuery] = useState("");
  const [day, setDay] = useState("");
  const [pending, setPending] = useState(null);

  useEffect(() => {
    dispatch(fetchMyBookings());
  }, [dispatch]);

  const today = todayIso();
  const days = Array.from({ length: BOOKING_WINDOW_DAYS + 1 }, (_, i) => addDaysIso(today, i));
  const cities = useMemo(() => [...new Set(items.map((k) => k.partner.city))].sort(), [items]);
  const bookedIds = new Set(mine.filter((b) => b.status === "CONFIRMED").map((b) => b.classId));

  const visible = items
    .filter((k) => !category || k.activity.category === category)
    .filter((k) => !city || k.partner.city === city)
    .filter((k) => !day || k.date === day)
    .filter((k) => !query || `${k.activity.name} ${k.partner.name}`.toLowerCase().includes(query.toLowerCase()));

  const confirm = async () => {
    const { ok } = await run(bookClass(pending.id), "Réservation confirmée");
    if (ok) dispatch(fetchBalance());
    setPending(null);
  };

  const byDay = Object.entries(Object.groupBy(visible, (k) => k.date));

  return (
    <>
      <PageHeader title="Activités" subtitle={`Réservable jusqu'à ${BOOKING_WINDOW_DAYS} jours à l'avance`} />
      <div className="mb-3 grid grid-cols-2 gap-2">
        <Input className="col-span-2" placeholder="Rechercher une activité ou un lieu" value={query} onChange={(e) => setQuery(e.target.value)} aria-label="Rechercher" />
        <Select value={category} onChange={(e) => setCategory(e.target.value)} options={CATEGORIES} placeholder="Toutes les activités" aria-label="Activité" />
        <Select value={city} onChange={(e) => setCity(e.target.value)} options={cities} placeholder="Toutes les villes" aria-label="Ville" />
      </div>
      <div className="mb-4 flex gap-2 overflow-x-auto pb-1">
        {[["", "Tous"], ...days.map((d) => [d, new Date(`${d}T00:00:00`).toLocaleDateString("fr-FR", { weekday: "short", day: "numeric" })])].map(([value, label]) => (
          <button
            key={value}
            type="button"
            onClick={() => setDay(value)}
            className={`shrink-0 rounded-full px-3 py-1 text-xs font-semibold ${day === value ? "bg-primary text-primary-foreground" : "bg-muted text-muted-foreground"}`}
          >
            {label}
          </button>
        ))}
      </div>

      <ErrorBanner message={error} />
      {loading && <Spinner />}
      {!loading && visible.length === 0 && <EmptyState>Aucune séance ne correspond à votre recherche.</EmptyState>}
      {byDay.map(([date, list]) => (
        <section key={date} className="mb-4">
          <h2 className="mb-2 text-sm font-bold capitalize">{longDate(date)}</h2>
          <div className="flex flex-col gap-2">
            {list.map((k) => (
              <ClassCard key={k.id} klass={k} booked={bookedIds.has(k.id)} busy={busy} onBook={setPending} />
            ))}
          </div>
        </section>
      ))}

      <Modal open={Boolean(pending)} title="Confirmer la réservation" onClose={() => setPending(null)}>
        {pending && (
          <>
            <p className="text-sm">
              {pending.activity.name} chez {pending.partner.name}, {longDate(pending.date)} à {shortTime(pending.startTime)}.
            </p>
            <p className="text-sm font-semibold">{pending.creditsRequired} crédits seront débités.</p>
            <Button onClick={confirm} disabled={busy}>Confirmer</Button>
          </>
        )}
      </Modal>
    </>
  );
}

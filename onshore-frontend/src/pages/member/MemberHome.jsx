import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import { PageHeader, ErrorBanner, Spinner, EmptyState } from "../../components/ui/Feedback";
import { useClassCatalog } from "../../hooks/useClassCatalog";
import { CATEGORIES, categoryImage, longDate, shortTime, todayIso } from "../../utils/format";

export default function MemberHome() {
  const user = useSelector((s) => s.auth.user);
  const { items, loading, error } = useClassCatalog();
  const today = todayIso();
  const upcoming = items.filter((k) => k.date >= today).slice(0, 5);

  return (
    <>
      <PageHeader title={`Bonjour ${user?.fullName?.split(" ")[0] ?? ""}`} subtitle="Votre côte, vos séances." />
      <ErrorBanner message={error} />

      <h2 className="mb-2 font-bold">Parcourir par activité</h2>
      <div className="mb-6 flex gap-3 overflow-x-auto pb-2">
        {CATEGORIES.map((c) => (
          <Link key={c} to={`/member/explorer?category=${encodeURIComponent(c)}`} className="w-24 shrink-0 text-center text-xs font-semibold">
            <img src={categoryImage(c)} alt="" className="mb-1 size-24 rounded-2xl object-cover" />
            {c}
          </Link>
        ))}
      </div>

      <h2 className="mb-2 font-bold">Prochaines séances</h2>
      {loading && <Spinner />}
      {!loading && upcoming.length === 0 && <EmptyState>Aucune séance ouverte pour le moment.</EmptyState>}
      <ul className="flex flex-col gap-2">
        {upcoming.map((k) => (
          <li key={k.id}>
            <Link to="/member/explorer" className="flex items-center justify-between rounded-xl border border-border bg-card p-3">
              <span>
                <span className="block font-semibold">{k.activity.name}</span>
                <span className="text-xs text-muted-foreground">
                  {k.partner.name} · {longDate(k.date)} · {shortTime(k.startTime)}
                </span>
              </span>
              <span className="text-xs font-bold text-primary">{k.creditsRequired} cr.</span>
            </Link>
          </li>
        ))}
      </ul>
    </>
  );
}

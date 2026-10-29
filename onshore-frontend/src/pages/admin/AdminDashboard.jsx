import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { ErrorBanner, PageHeader, Spinner, StatCard } from "../../components/ui/Feedback";
import { fetchDashboard } from "../../Redux/admin/adminSlice";
import { money } from "../../utils/format";

export default function AdminDashboard() {
  const dispatch = useDispatch();
  const { dashboard, loading, error } = useSelector((s) => s.admin);

  useEffect(() => {
    dispatch(fetchDashboard());
  }, [dispatch]);

  if (!dashboard) return error ? <ErrorBanner message={error} /> : <Spinner />;
  const { bookings, credits, payouts, revenue, margin } = dashboard;

  return (
    <>
      <PageHeader title="Tableau de bord" subtitle={loading ? "Actualisation..." : "Vue globale ON SHORE CLUB"} />
      <div className="grid grid-cols-2 gap-3 md:grid-cols-4">
        <StatCard label="Chiffre d'affaires" value={money(revenue)} hint="abonnements + packs" />
        <StatCard label="Marge" value={money(margin)} hint="CA - reversements partenaires" />
        <StatCard label="Reversements" value={money(payouts.totalAmount)} hint={`${money(payouts.pendingAmount)} en attente`} />
        <StatCard label="Abonnés actifs" value={credits.activeSubscriptions} hint={`${credits.creditsInCirculation} crédits en circulation`} />
        <StatCard label="Réservations" value={bookings.total} hint={`${bookings.confirmed} à venir`} />
        <StatCard label="Check-ins" value={bookings.completed} hint={`${bookings.qrCheckins} QR · ${bookings.manualCheckins} manuels`} />
        <StatCard label="No-shows" value={bookings.noShows} />
        <StatCard label="Annulations" value={bookings.cancelled} />
      </div>
    </>
  );
}

import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { EmptyState, ErrorBanner, PageHeader } from "../../components/ui/Feedback";
import { Badge } from "../../components/ui/Badge";
import { bookingTone } from "../../utils/tones";
import { fetchPartnerBookings } from "../../Redux/booking/bookingSlice";
import { fetchActivities, fetchPartners } from "../../Redux/catalog/catalogSlice";
import { BOOKING_STATUS_LABEL, addDaysIso, longDate, shortTime, todayIso } from "../../utils/format";

export default function AdminBookings() {
  const dispatch = useDispatch();
  const { managed, error } = useSelector((s) => s.booking);
  const { partners, activities } = useSelector((s) => s.catalog);

  useEffect(() => {
    const today = todayIso();
    dispatch(fetchPartnerBookings({ from: addDaysIso(today, -30), to: addDaysIso(today, 8) }));
    dispatch(fetchPartners({}));
    dispatch(fetchActivities({}));
  }, [dispatch]);

  return (
    <>
      <PageHeader title="Réservations" subtitle="30 jours passés et fenêtre de réservation" />
      <ErrorBanner message={error} />
      {managed.length === 0 && <EmptyState>Aucune réservation.</EmptyState>}
      <ul className="flex flex-col gap-2">
        {managed.map((b) => (
          <li key={b.id} className="flex items-center justify-between rounded-xl border border-border bg-card p-3 text-sm">
            <span>
              <span className="block font-semibold">{activities.find((a) => a.id === b.activityId)?.name ?? "Activité"} · {partners.find((p) => p.id === b.partnerId)?.name ?? ""}</span>
              <span className="text-xs text-muted-foreground">
                {longDate(b.classDate)} · {shortTime(b.classStartTime)} · {b.creditsSpent} crédits · {b.checkinMethod ?? "pas de check-in"}
              </span>
            </span>
            <Badge tone={bookingTone(b.status)}>{BOOKING_STATUS_LABEL[b.status]}</Badge>
          </li>
        ))}
      </ul>
    </>
  );
}

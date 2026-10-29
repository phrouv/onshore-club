import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PageHeader, ErrorBanner, Spinner, EmptyState } from "../../components/ui/Feedback";
import { Card } from "../../components/ui/Card";
import { Badge } from "../../components/ui/Badge";
import { bookingTone } from "../../utils/tones";
import { Button } from "../../components/ui/Button";
import { Field, Input } from "../../components/ui/Field";
import { Modal } from "../../components/ui/Modal";
import { useAction } from "../../hooks/useAction";
import { cancelBooking, checkInBooking, fetchMyBookings } from "../../Redux/booking/bookingSlice";
import { fetchActivities, fetchPartners } from "../../Redux/catalog/catalogSlice";
import { fetchBalance } from "../../Redux/credit/creditSlice";
import { BOOKING_STATUS_LABEL, longDate, shortTime, todayIso } from "../../utils/format";

export default function Planning() {
  const dispatch = useDispatch();
  const { mine, loading, error } = useSelector((s) => s.booking);
  const { activities, partners } = useSelector((s) => s.catalog);
  const { run, busy } = useAction();
  const [cancelTarget, setCancelTarget] = useState(null);
  const [checkinTarget, setCheckinTarget] = useState(null);
  const [code, setCode] = useState("");

  useEffect(() => {
    dispatch(fetchMyBookings());
    dispatch(fetchActivities({}));
    dispatch(fetchPartners({}));
  }, [dispatch]);

  const activityName = (id) => activities.find((a) => a.id === id)?.name ?? "Activité";
  const partnerName = (id) => partners.find((p) => p.id === id)?.name ?? "";
  const today = todayIso();
  const upcoming = mine.filter((b) => b.status === "CONFIRMED" && b.classDate >= today);
  const past = mine.filter((b) => !upcoming.includes(b));

  const doCancel = async () => {
    const { ok } = await run(cancelBooking(cancelTarget.id), "Réservation annulée");
    if (ok) dispatch(fetchBalance());
    setCancelTarget(null);
  };

  const doCheckin = async (event) => {
    event.preventDefault();
    const { ok } = await run(checkInBooking({ id: checkinTarget.id, code }), "Check-in effectué");
    if (ok) {
      setCheckinTarget(null);
      setCode("");
    }
  };

  const row = (b, actions) => (
    <Card key={b.id} className="flex flex-col gap-2">
      <div className="flex items-start justify-between">
        <div>
          <p className="font-bold">{activityName(b.activityId)}</p>
          <p className="text-xs text-muted-foreground">
            {partnerName(b.partnerId)} · {longDate(b.classDate)} · {shortTime(b.classStartTime)}
          </p>
        </div>
        <Badge tone={bookingTone(b.status)}>{BOOKING_STATUS_LABEL[b.status]}</Badge>
      </div>
      <p className="text-xs text-muted-foreground">Code de réservation : <span className="font-mono font-bold">{b.code}</span></p>
      {actions}
    </Card>
  );

  return (
    <>
      <PageHeader title="Mon agenda" />
      <ErrorBanner message={error} />
      {loading && mine.length === 0 && <Spinner />}

      <h2 className="mb-2 font-bold">À venir</h2>
      {upcoming.length === 0 && <EmptyState>Aucune réservation à venir.</EmptyState>}
      <div className="mb-6 flex flex-col gap-2">
        {upcoming.map((b) =>
          row(
            b,
            <div className="flex gap-2">
              <Button size="sm" onClick={() => setCheckinTarget(b)}>Faire mon check-in</Button>
              <Button size="sm" variant="outline" onClick={() => setCancelTarget(b)}>Annuler</Button>
            </div>,
          ),
        )}
      </div>

      <h2 className="mb-2 font-bold">Passées</h2>
      <div className="flex flex-col gap-2">{past.map((b) => row(b))}</div>

      <Modal open={Boolean(cancelTarget)} title="Annuler la réservation ?" onClose={() => setCancelTarget(null)}>
        <p className="text-sm">Les crédits sont restitués uniquement si l'annulation est faite dans le délai défini par le club (12 h avant le cours par défaut).</p>
        <Button variant="destructive" onClick={doCancel} disabled={busy}>Confirmer l'annulation</Button>
      </Modal>

      <Modal open={Boolean(checkinTarget)} title="Check-in" onClose={() => setCheckinTarget(null)}>
        <form onSubmit={doCheckin} className="flex flex-col gap-3">
          <Field label="Code du partenaire" hint="Saisissez le code affiché à l'accueil du club (ONEPASS:XXXXXXXX ou XXXXXXXX).">
            <Input required value={code} onChange={(e) => setCode(e.target.value)} />
          </Field>
          <Button type="submit" disabled={busy}>Valider</Button>
        </form>
      </Modal>
    </>
  );
}

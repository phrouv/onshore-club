import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { QRCodeSVG } from "qrcode.react";
import { PartnerGate } from "../../components/partner/PartnerGate";
import { EmptyState, PageHeader } from "../../components/ui/Feedback";
import { Button } from "../../components/ui/Button";
import { Card, CardTitle } from "../../components/ui/Card";
import { Badge } from "../../components/ui/Badge";
import { bookingTone } from "../../utils/tones";
import { Field, Input } from "../../components/ui/Field";
import { useAction } from "../../hooks/useAction";
import { fetchPartnerBookings, validateBookingCode } from "../../Redux/booking/bookingSlice";
import { fetchPartnerActivities } from "../../Redux/partner/partnerSlice";
import { BOOKING_STATUS_LABEL, addDaysIso, longDate, shortTime, todayIso } from "../../utils/format";

function Bookings({ partner }) {
  const dispatch = useDispatch();
  const bookings = useSelector((s) => s.booking.managed);
  const activities = useSelector((s) => s.partner.activities);
  const { run, busy } = useAction();
  const [code, setCode] = useState("");
  const partnerCode = partner.id.slice(0, 8).toUpperCase();

  useEffect(() => {
    const today = todayIso();
    dispatch(fetchPartnerActivities(partner.id));
    dispatch(fetchPartnerBookings({ partnerId: partner.id, from: addDaysIso(today, -7), to: addDaysIso(today, 8) }));
  }, [dispatch, partner.id]);

  const validate = async (event) => {
    event.preventDefault();
    const { ok } = await run(validateBookingCode(code), "Check-in validé");
    if (ok) setCode("");
  };

  return (
    <>
      <PageHeader title="Réservations" />
      <div className="mb-6 grid gap-3 md:grid-cols-2">
        <Card className="flex items-center gap-4">
          <QRCodeSVG value={`ONEPASS:${partnerCode}`} size={112} />
          <div>
            <CardTitle>QR code de l'établissement</CardTitle>
            <p className="text-sm text-muted-foreground">À imprimer à l'accueil : le membre le scanne pour son check-in.</p>
            <p className="font-mono text-sm font-bold">ONEPASS:{partnerCode}</p>
          </div>
        </Card>
        <Card>
          <CardTitle>Validation manuelle de secours</CardTitle>
          <form onSubmit={validate} className="mt-2 flex items-end gap-2">
            <div className="flex-1"><Field label="Code de réservation"><Input required value={code} onChange={(e) => setCode(e.target.value)} /></Field></div>
            <Button type="submit" disabled={busy}>Valider</Button>
          </form>
        </Card>
      </div>

      {bookings.length === 0 && <EmptyState>Aucune réservation sur la période.</EmptyState>}
      <ul className="flex flex-col gap-2">
        {bookings.map((b) => (
          <li key={b.id} className="flex items-center justify-between rounded-xl border border-border bg-card p-3 text-sm">
            <span>
              <span className="block font-semibold">{activities.find((a) => a.id === b.activityId)?.name ?? "Activité"}</span>
              <span className="text-xs text-muted-foreground">{longDate(b.classDate)} · {shortTime(b.classStartTime)} · code {b.code}</span>
            </span>
            <Badge tone={bookingTone(b.status)}>{BOOKING_STATUS_LABEL[b.status]}</Badge>
          </li>
        ))}
      </ul>
    </>
  );
}

export default function PartnerBookings() {
  return <PartnerGate>{(partner) => <Bookings partner={partner} />}</PartnerGate>;
}

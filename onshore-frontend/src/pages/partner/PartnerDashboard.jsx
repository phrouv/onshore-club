import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PartnerGate } from "../../components/partner/PartnerGate";
import { PageHeader, StatCard } from "../../components/ui/Feedback";
import { fetchPartnerBookings } from "../../Redux/booking/bookingSlice";
import { fetchMonthly, fetchPartnerClasses } from "../../Redux/partner/partnerSlice";
import { addDaysIso, money, todayIso } from "../../utils/format";

function Dashboard({ partner }) {
  const dispatch = useDispatch();
  const { classes, monthly } = useSelector((s) => s.partner);
  const bookings = useSelector((s) => s.booking.managed);

  useEffect(() => {
    const today = todayIso();
    dispatch(fetchPartnerClasses({ partnerId: partner.id, from: today, to: addDaysIso(today, 30) }));
    dispatch(fetchPartnerBookings({ partnerId: partner.id, from: addDaysIso(today, -30), to: addDaysIso(today, 8) }));
    dispatch(fetchMonthly(partner.id));
  }, [dispatch, partner.id]);

  const thisMonth = monthly.find((m) => m.month === todayIso().slice(0, 7));
  return (
    <>
      <PageHeader title={partner.name} subtitle={partner.city} />
      <div className="grid grid-cols-2 gap-3 md:grid-cols-4">
        <StatCard label="Créneaux (30 j)" value={classes.length} />
        <StatCard label="Réservations" value={bookings.filter((b) => b.status === "CONFIRMED").length} hint="à venir" />
        <StatCard label="Check-ins" value={bookings.filter((b) => b.status === "COMPLETED").length} hint="30 derniers jours" />
        <StatCard label="Gains du mois" value={money(thisMonth?.total ?? 0)} hint={`${money(thisMonth?.pending ?? 0)} en attente`} />
      </div>
    </>
  );
}

export default function PartnerDashboard() {
  return <PartnerGate>{(partner) => <Dashboard partner={partner} />}</PartnerGate>;
}

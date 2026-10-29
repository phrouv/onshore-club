import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PartnerGate } from "../../components/partner/PartnerGate";
import { EmptyState, PageHeader } from "../../components/ui/Feedback";
import { Badge } from "../../components/ui/Badge";
import { fetchMonthly, fetchPayouts } from "../../Redux/partner/partnerSlice";
import { PAYOUT_STATUS_LABEL, dateTime, money } from "../../utils/format";

function Earnings({ partner }) {
  const dispatch = useDispatch();
  const { monthly, payouts } = useSelector((s) => s.partner);

  useEffect(() => {
    dispatch(fetchMonthly(partner.id));
    dispatch(fetchPayouts(partner.id));
  }, [dispatch, partner.id]);

  return (
    <>
      <PageHeader title="Gains" subtitle="Un montant fixe par check-in, versé pour chaque séance validée." />
      <h2 className="mb-2 font-bold">Par mois</h2>
      {monthly.length === 0 && <EmptyState>Aucun gain pour le moment.</EmptyState>}
      <div className="mb-6 grid gap-2 md:grid-cols-3">
        {monthly.map((m) => (
          <div key={m.month} className="rounded-2xl border border-border bg-card p-4">
            <p className="text-xs font-semibold uppercase text-muted-foreground">{m.month}</p>
            <p className="text-2xl font-extrabold">{money(m.total)}</p>
            <p className="text-xs text-muted-foreground">{m.count} séance(s) · {money(m.pending)} en attente · {money(m.paid)} payés</p>
          </div>
        ))}
      </div>
      <h2 className="mb-2 font-bold">Historique des reversements</h2>
      <ul className="flex flex-col gap-2">
        {payouts.map((p) => (
          <li key={p.id} className="flex items-center justify-between rounded-xl border border-border bg-card p-3 text-sm">
            <span>{dateTime(p.createdAt)}</span>
            <span className="flex items-center gap-3">
              <strong>{money(p.amount)}</strong>
              <Badge tone={p.status === "PAID" ? "success" : "warning"}>{PAYOUT_STATUS_LABEL[p.status]}</Badge>
            </span>
          </li>
        ))}
      </ul>
    </>
  );
}

export default function PartnerEarnings() {
  return <PartnerGate>{(partner) => <Earnings partner={partner} />}</PartnerGate>;
}

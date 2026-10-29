import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { EmptyState, ErrorBanner, PageHeader } from "../../components/ui/Feedback";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { useAction } from "../../hooks/useAction";
import { fetchAllPartners, fetchAllPayouts, fetchPayoutSummary, setPayoutStatus } from "../../Redux/admin/adminSlice";
import { PAYOUT_STATUS_LABEL, dateTime, money } from "../../utils/format";

export default function AdminPayouts() {
  const dispatch = useDispatch();
  const { payouts, payoutSummary, partners, error } = useSelector((s) => s.admin);
  const { run, busy } = useAction();

  useEffect(() => {
    dispatch(fetchAllPayouts());
    dispatch(fetchPayoutSummary());
    dispatch(fetchAllPartners());
  }, [dispatch]);

  const partnerName = (id) => partners.find((p) => p.id === id)?.name ?? id.slice(0, 8);

  const toggle = async (payout) => {
    const { ok } = await run(setPayoutStatus({ id: payout.id, status: payout.status === "PAID" ? "PENDING" : "PAID" }));
    if (ok) dispatch(fetchPayoutSummary());
  };

  return (
    <>
      <PageHeader title="Paiements partenaires" subtitle="Montant fixe par check-in, sans lien avec les crédits dépensés." />
      <ErrorBanner message={error} />
      <h2 className="mb-2 font-bold">Par partenaire</h2>
      {payoutSummary.length === 0 && <EmptyState>Aucun reversement.</EmptyState>}
      <div className="mb-6 grid gap-2 md:grid-cols-3">
        {payoutSummary.map((s) => (
          <div key={s.partnerId} className="rounded-2xl border border-border bg-card p-4">
            <p className="font-bold">{partnerName(s.partnerId)}</p>
            <p className="text-xl font-extrabold">{money(s.total)}</p>
            <p className="text-xs text-muted-foreground">{s.count} check-in(s) · {money(s.pending)} en attente · {money(s.paid)} payés</p>
          </div>
        ))}
      </div>
      <h2 className="mb-2 font-bold">Détail</h2>
      <ul className="flex flex-col gap-2">
        {payouts.map((p) => (
          <li key={p.id} className="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-border bg-card p-3 text-sm">
            <span>{partnerName(p.partnerId)} · {dateTime(p.createdAt)}</span>
            <span className="flex items-center gap-3">
              <strong>{money(p.amount)}</strong>
              <Badge tone={p.status === "PAID" ? "success" : "warning"}>{PAYOUT_STATUS_LABEL[p.status]}</Badge>
              <Button size="sm" variant="outline" disabled={busy} onClick={() => toggle(p)}>{p.status === "PAID" ? "Remettre en attente" : "Marquer payé"}</Button>
            </span>
          </li>
        ))}
      </ul>
    </>
  );
}

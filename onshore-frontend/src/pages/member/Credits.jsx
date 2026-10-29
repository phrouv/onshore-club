import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PageHeader, ErrorBanner, EmptyState } from "../../components/ui/Feedback";
import { Card, CardTitle } from "../../components/ui/Card";
import { Button } from "../../components/ui/Button";
import { Badge } from "../../components/ui/Badge";
import { useAction } from "../../hooks/useAction";
import { cancelPlan, changePlan, fetchBalance, fetchPlans, fetchSubscription, fetchTransactions, purchaseCredits } from "../../Redux/credit/creditSlice";
import { dateTime, money, TX_LABEL } from "../../utils/format";

const PACKS = [
  { credits: 10, price: 15 },
  { credits: 25, price: 35 },
];

export default function Credits() {
  const dispatch = useDispatch();
  const { balance, transactions, plans, subscription, error } = useSelector((s) => s.credit);
  const { run, busy } = useAction();

  useEffect(() => {
    dispatch(fetchTransactions());
    dispatch(fetchPlans());
    dispatch(fetchSubscription());
  }, [dispatch]);

  const refresh = () => {
    dispatch(fetchTransactions());
  };

  const buy = async (pack) => {
    const { ok } = await run(purchaseCredits(pack), `${pack.credits} crédits ajoutés`);
    if (ok) refresh();
  };
  const choose = async (plan) => {
    const { ok } = await run(changePlan(plan.id), `Formule ${plan.name} activée`);
    if (ok) {
      dispatch(fetchTransactions());
      dispatch(fetchBalance());
    }
  };

  return (
    <>
      <PageHeader title="Mes crédits" />
      <ErrorBanner message={error} />
      <Card className="mb-4 bg-primary text-primary-foreground">
        <p className="text-sm opacity-80">Solde</p>
        <p className="text-4xl font-extrabold">{balance}</p>
        {subscription && <p className="text-sm opacity-80">Formule {subscription.planName} · renouvellement le {subscription.endDate}</p>}
      </Card>

      <h2 className="mb-2 font-bold">Formules</h2>
      <div className="mb-6 grid gap-2">
        {plans.map((p) => {
          const current = subscription?.planId === p.id;
          return (
            <Card key={p.id} className="flex items-center justify-between">
              <div>
                <CardTitle>
                  {p.name} {p.popular && <Badge tone="warning">Populaire</Badge>}
                </CardTitle>
                <p className="text-xs text-muted-foreground">{p.positioning}</p>
                <p className="text-sm">{money(p.monthlyPrice)} / mois · {p.creditsPerMonth} crédits</p>
              </div>
              {current ? (
                <Button size="sm" variant="outline" disabled={busy} onClick={() => run(cancelPlan(), "Abonnement résilié")}>Résilier</Button>
              ) : (
                <Button size="sm" variant="accent" disabled={busy} onClick={() => choose(p)}>Choisir</Button>
              )}
            </Card>
          );
        })}
      </div>

      <h2 className="mb-2 font-bold">Recharger</h2>
      <div className="mb-6 flex gap-2">
        {PACKS.map((pack) => (
          <Button key={pack.credits} variant="outline" className="flex-1" disabled={busy} onClick={() => buy(pack)}>
            +{pack.credits} crédits · {money(pack.price)}
          </Button>
        ))}
      </div>

      <h2 className="mb-2 font-bold">Historique</h2>
      {transactions.length === 0 && <EmptyState>Aucun mouvement.</EmptyState>}
      <ul className="flex flex-col gap-2">
        {transactions.map((t) => (
          <li key={t.id} className="flex items-center justify-between rounded-xl border border-border bg-card p-3 text-sm">
            <span>
              <span className="block font-semibold">{TX_LABEL[t.type] ?? t.type}</span>
              <span className="text-xs text-muted-foreground">{t.description} · {dateTime(t.createdAt)}</span>
            </span>
            <span className={t.amount < 0 ? "font-bold text-destructive" : "font-bold text-emerald-700"}>{t.amount > 0 ? `+${t.amount}` : t.amount}</span>
          </li>
        ))}
      </ul>
    </>
  );
}

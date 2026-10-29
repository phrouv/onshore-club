import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PageHeader } from "../../components/ui/Feedback";
import { Card } from "../../components/ui/Card";
import { Button } from "../../components/ui/Button";
import { logout } from "../../Redux/auth/authSlice";
import { fetchSubscription } from "../../Redux/credit/creditSlice";
import { money } from "../../utils/format";

export default function MemberProfile() {
  const dispatch = useDispatch();
  const user = useSelector((s) => s.auth.user);
  const { subscription, balance } = useSelector((s) => s.credit);

  useEffect(() => {
    dispatch(fetchSubscription());
  }, [dispatch]);

  return (
    <>
      <PageHeader title="Mon Club" />
      <Card className="mb-3">
        <p className="text-lg font-bold">{user?.fullName}</p>
        <p className="text-sm text-muted-foreground">{user?.email}</p>
      </Card>
      <Card className="mb-6">
        <p className="font-bold">Abonnement</p>
        {subscription ? (
          <p className="text-sm">{subscription.planName} · {money(subscription.monthlyPrice)} / mois · {subscription.creditsPerMonth} crédits · {balance} restants</p>
        ) : (
          <p className="text-sm text-muted-foreground">Aucune formule active.</p>
        )}
      </Card>
      <Button variant="outline" onClick={() => dispatch(logout())}>Se déconnecter</Button>
    </>
  );
}

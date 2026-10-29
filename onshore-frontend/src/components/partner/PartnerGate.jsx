import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchMyPartner } from "../../Redux/partner/partnerSlice";
import { PartnerForm } from "./PartnerForm";
import { ErrorBanner, PageHeader, Spinner } from "../ui/Feedback";
import { Badge } from "../ui/Badge";
import { partnerTone } from "../../utils/tones";
import { PARTNER_STATUS_LABEL } from "../../utils/format";

/** Loads the partner of the current user and only renders children once it is approved. */
export function PartnerGate({ children, allowPending = false }) {
  const dispatch = useDispatch();
  const { partner, partnerLoaded, error } = useSelector((s) => s.partner);

  useEffect(() => {
    dispatch(fetchMyPartner());
  }, [dispatch]);

  if (!partnerLoaded) return error ? <ErrorBanner message={error} /> : <Spinner />;

  if (!partner) {
    return (
      <>
        <PageHeader title="Devenir partenaire" subtitle="Complétez votre demande : elle sera étudiée par l'équipe ON SHORE CLUB." />
        <PartnerForm partner={null} />
      </>
    );
  }

  if (partner.status !== "ACTIVE" && !allowPending) {
    return (
      <>
        <PageHeader title={partner.name} />
        <p className="flex items-center gap-2 text-sm">
          Statut de votre demande : <Badge tone={partnerTone(partner.status)}>{PARTNER_STATUS_LABEL[partner.status]}</Badge>
        </p>
        <p className="mt-2 text-sm text-muted-foreground">Vous aurez accès à vos activités et créneaux dès la validation de votre compte.</p>
      </>
    );
  }

  return typeof children === "function" ? children(partner) : children;
}

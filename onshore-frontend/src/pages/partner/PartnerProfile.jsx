import { PartnerGate } from "../../components/partner/PartnerGate";
import { PageHeader } from "../../components/ui/Feedback";
import { Badge } from "../../components/ui/Badge";
import { partnerTone } from "../../utils/tones";
import { PartnerForm } from "../../components/partner/PartnerForm";
import { PARTNER_STATUS_LABEL } from "../../utils/format";

export default function PartnerProfile() {
  return (
    <PartnerGate allowPending>
      {(partner) => (
        <>
          <PageHeader title="Profil" actions={<Badge tone={partnerTone(partner.status)}>{PARTNER_STATUS_LABEL[partner.status]}</Badge>} />
          <PartnerForm partner={partner} />
        </>
      )}
    </PartnerGate>
  );
}

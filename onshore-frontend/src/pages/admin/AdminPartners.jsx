import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  EmptyState,
  ErrorBanner,
  PageHeader,
} from "../../components/ui/Feedback";
import { Badge } from "../../components/ui/Badge";
import { partnerTone } from "../../utils/tones";
import { Button } from "../../components/ui/Button";
import { useAction } from "../../hooks/useAction";
import { Pencil, Power } from "lucide-react";
import {
  createPartner,
  editPartner,
  fetchAllPartners,
  fetchPayoutSummary,
  setPartnerStatus,
} from "../../Redux/admin/adminSlice";
import { PARTNER_STATUS_LABEL, money } from "../../utils/format";
import { Modal } from "../../components/ui/Modal";
import { Field, Input, Textarea } from "../../components/ui/Field";

const EMPTY = {
  name: "",
  city: "",
  street: "",
  zipCode: "",
  description: "",
  website: "",
  instagram: "",
  managerFirstName: "",
  managerLastName: "",
  managerEmail: "",
  managerPhoneNumber: "",
  active: true,
};

const FORM_FIELDS = [
  "name",
  "city",
  "street",
  "zipCode",
  "description",
  "website",
  "instagram",
  "managerFirstName",
  "managerLastName",
  "managerEmail",
  "managerPhoneNumber",
];

function PartnerModal({ open, partner, onClose }) {
  const { run, busy } = useAction();
  const [form, setForm] = useState(() =>
    partner
      ? {
          ...Object.fromEntries(FORM_FIELDS.map((k) => [k, partner[k] ?? ""])),
          active: partner.status === "ACTIVE",
        }
      : EMPTY,
  );
  const [errors, setErrors] = useState({});

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const close = () => {
    if (!partner) setForm(EMPTY);
    setErrors({});
    onClose();
  };

  const submit = async (event) => {
    event.preventDefault();
    const next = {};
    for (const key of ["name", "city", "street", "zipCode"]) {
      if (!form[key].trim()) next[key] = "Champ obligatoire";
    }
    setErrors(next);
    if (Object.keys(next).length) return;
    const { active, ...rest } = form;
    const body = { ...rest, description: rest.description || null };
    const { ok } = partner
      ? await run(
          editPartner({
            id: partner.id,
            body,
            status:
              active === (partner.status === "ACTIVE")
                ? null
                : active
                  ? "ACTIVE"
                  : "SUSPENDED",
          }),
          "Partenaire mis à jour",
        )
      : await run(createPartner({ body, active }), "Partenaire créé");
    if (ok) close();
  };

  return (
    <Modal
      open={open}
      title={partner ? "Modifier le partenaire" : "Nouveau partenaire"}
      onClose={close}
    >
      <form onSubmit={submit} className="grid grid-cols-2 gap-3">
        <Field label="Nom" error={errors.name}>
          <Input value={form.name} onChange={set("name")} />
        </Field>
        <Field label="Ville" error={errors.city}>
          <Input value={form.city} onChange={set("city")} />
        </Field>
        <Field label="Adresse" error={errors.street}>
          <Input value={form.street} onChange={set("street")} />
        </Field>
        <Field label="Code postal" error={errors.zipCode}>
          <Input value={form.zipCode} onChange={set("zipCode")} />
        </Field>
        <div className="col-span-2">
          <Field label="Description">
            <Textarea
              rows={4}
              value={form.description}
              onChange={set("description")}
            />
          </Field>
        </div>
        <Field label="Site web">
          <Input value={form.website} onChange={set("website")} />
        </Field>
        <Field label="Instagram">
          <Input value={form.instagram} onChange={set("instagram")} />
        </Field>
        <div className="col-span-2 flex flex-col gap-3 rounded-xl border border-border p-3">
          <p className="text-sm font-semibold">Manager</p>
          <div className="grid grid-cols-2 gap-3">
            <Field label="Prénom">
              <Input
                value={form.managerFirstName}
                onChange={set("managerFirstName")}
              />
            </Field>
            <Field label="Nom">
              <Input
                value={form.managerLastName}
                onChange={set("managerLastName")}
              />
            </Field>
            <Field label="Email">
              <Input
                type="email"
                value={form.managerEmail}
                onChange={set("managerEmail")}
              />
            </Field>
            <Field label="Téléphone">
              <Input
                value={form.managerPhoneNumber}
                onChange={set("managerPhoneNumber")}
              />
            </Field>
          </div>
        </div>
        <label className="col-span-2 flex items-center justify-between rounded-xl bg-muted p-3 text-sm font-medium">
          Partenaire actif
          <input
            type="checkbox"
            role="switch"
            checked={form.active}
            onChange={(e) => setForm({ ...form, active: e.target.checked })}
            className="h-6 w-11 cursor-pointer appearance-none rounded-full bg-border transition-colors checked:bg-primary relative before:absolute before:top-0.5 before:left-0.5 before:size-5 before:rounded-full before:bg-card before:transition-transform checked:before:translate-x-5"
          />
        </label>
        <div className="col-span-2 flex items-center justify-end gap-3">
          <Button type="button" variant="ghost" onClick={close}>
            Annuler
          </Button>
          <Button type="submit" disabled={busy}>
            Enregistrer
          </Button>
        </div>
      </form>
    </Modal>
  );
}

export default function AdminPartners() {
  const dispatch = useDispatch();
  const { partners, payoutSummary, error } = useSelector((s) => s.admin);
  const { run, busy } = useAction();
  const [creating, setCreating] = useState(false);
  const [editing, setEditing] = useState(null);

  useEffect(() => {
    dispatch(fetchAllPartners());
    dispatch(fetchPayoutSummary());
  }, [dispatch]);

  const change = (id, status) =>
    run(
      setPartnerStatus({ id, status }),
      `Statut : ${PARTNER_STATUS_LABEL[status]}`,
    );

  return (
    <>
      <PageHeader
        title="Partenaires"
        subtitle="Clubs, activités, créneaux, check-ins et rémunérations"
        actions={
          <Button onClick={() => setCreating(true)}>
            + Nouveau partenaire
          </Button>
        }
      />
      <PartnerModal open={creating} onClose={() => setCreating(false)} />
      {editing && (
        <PartnerModal open partner={editing} onClose={() => setEditing(null)} />
      )}
      <ErrorBanner message={error} />
      {partners.length === 0 && <EmptyState>Aucun partenaire.</EmptyState>}
      {partners.length > 0 && (
        <div className="overflow-x-auto rounded-2xl border border-border bg-card">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-border text-muted-foreground">
              <tr>
                {[
                  "Partenaire",
                  "Adresse",
                  "Site web",
                  "Instagram",
                  "Rémunérations",
                  "Statut",
                  "",
                ].map((h) => (
                  <th key={h} className="px-3 py-3 font-medium">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {partners.map((p) => {
                const stats = payoutSummary.find((s) => s.partnerId === p.id);
                return (
                  <tr
                    key={p.id}
                    className="border-b border-border last:border-0"
                  >
                    <td className="px-3 py-4 font-medium">{p.name}</td>
                    <td className="px-3 py-4 text-muted-foreground">
                      {p.street}, {p.zipCode} {p.city}
                    </td>
                    <td className="px-3 py-4 text-muted-foreground">
                      {p.website || "—"}
                    </td>
                    <td className="px-3 py-4 text-muted-foreground">
                      {p.instagram || "—"}
                    </td>
                    <td className="px-3 py-4 font-semibold">
                      {money(stats?.total)}
                    </td>
                    <td className="px-3 py-4">
                      <Badge tone={partnerTone(p.status)}>
                        {PARTNER_STATUS_LABEL[p.status].toLowerCase()}
                      </Badge>
                    </td>
                    <td className="px-3 py-4">
                      <div className="flex justify-end gap-1">
                        <Button
                          size="sm"
                          variant="ghost"
                          aria-label="Modifier"
                          title="Modifier"
                          onClick={() => setEditing(p)}
                        >
                          <Pencil className="size-4" />
                        </Button>
                        {p.status !== "ACTIVE" && (
                          <Button
                            size="sm"
                            variant="ghost"
                            disabled={busy}
                            onClick={() => change(p.id, "ACTIVE")}
                          >
                            {p.status === "PENDING" ? "Approuver" : "Activer"}
                          </Button>
                        )}
                        {p.status === "PENDING" && (
                          <Button
                            size="sm"
                            variant="ghost"
                            disabled={busy}
                            onClick={() => change(p.id, "REJECTED")}
                          >
                            Refuser
                          </Button>
                        )}
                        {p.status === "ACTIVE" && (
                          <Button
                            size="sm"
                            variant="ghost"
                            aria-label="Désactiver"
                            title="Désactiver"
                            disabled={busy}
                            onClick={() => change(p.id, "SUSPENDED")}
                          >
                            <Power className="size-4" />
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}

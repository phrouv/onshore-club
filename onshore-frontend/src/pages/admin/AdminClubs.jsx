import { useEffect, useMemo, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { EmptyState, ErrorBanner } from "../../components/ui/Feedback";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { Field, Input, Select, Textarea } from "../../components/ui/Field";
import { Modal } from "../../components/ui/Modal";
import { useAction } from "../../hooks/useAction";
import { Pencil, Power } from "lucide-react";
import {
  fetchAllPartners,
  fetchClubs,
  saveClub,
  setClubOpen,
} from "../../Redux/admin/adminSlice";

const EMPTY = {
  partnerId: "",
  name: "",
  street: "",
  city: "",
  zipCode: "",
  latitude: "",
  longitude: "",
  description: "",
  contactFirstName: "",
  contactLastName: "",
  contactEmail: "",
  contactPhoneNumber: "",
};

function ClubModal({ open, club, partners, onClose }) {
  const { run, busy } = useAction();
  const [form, setForm] = useState(() =>
    club
      ? {
          partnerId: club.partnerId ?? "",
          name: club.name ?? "",
          street: club.street ?? "",
          city: club.city ?? "",
          zipCode: club.zipCode ?? "",
          latitude: club.latitude ?? "",
          longitude: club.longitude ?? "",
          description: club.description ?? "",
          contactFirstName: club.contactFirstName ?? "",
          contactLastName: club.contactLastName ?? "",
          contactEmail: club.contactEmail ?? "",
          contactPhoneNumber: club.contactPhoneNumber ?? "",
        }
      : EMPTY,
  );
  const [errors, setErrors] = useState({});

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const close = () => {
    if (!club) setForm(EMPTY);
    setErrors({});
    onClose();
  };

  const submit = async (event) => {
    event.preventDefault();
    const next = {};
    if (!form.partnerId) next.partnerId = "Champ obligatoire";
    for (const key of ["name", "street", "city", "zipCode"]) {
      if (!form[key].trim()) next[key] = "Champ obligatoire";
    }
    const latitude = form.latitude === "" ? null : Number(form.latitude);
    const longitude = form.longitude === "" ? null : Number(form.longitude);
    if (latitude != null && (latitude < -90 || latitude > 90)) {
      next.latitude = "Entre -90 et 90";
    }
    if (longitude != null && (longitude < -180 || longitude > 180)) {
      next.longitude = "Entre -180 et 180";
    }
    if (form.contactEmail && !/^\S+@\S+\.\S+$/.test(form.contactEmail)) {
      next.contactEmail = "Email invalide";
    }
    setErrors(next);
    if (Object.keys(next).length) return;
    const body = {
      partnerId: form.partnerId,
      name: form.name,
      street: form.street,
      city: form.city,
      zipCode: form.zipCode,
      latitude,
      longitude,
      description: form.description || null,
      contactFirstName: form.contactFirstName || null,
      contactLastName: form.contactLastName || null,
      contactEmail: form.contactEmail || null,
      contactPhoneNumber: form.contactPhoneNumber || null,
    };
    const { ok } = await run(
      club ? saveClub({ id: club.id, body }) : saveClub({ body }),
      club ? "Club mis à jour" : "Club créé",
    );
    if (ok) close();
  };

  return (
    <Modal
      open={open}
      title={club ? "Modifier le club" : "Nouveau club"}
      onClose={close}
    >
      <form onSubmit={submit} className="flex flex-col gap-3">
        <Field label="Partenaire" error={errors.partnerId}>
          <Select
            placeholder="Choisir un partenaire"
            value={form.partnerId}
            onChange={set("partnerId")}
            options={partners.map((p) => ({ value: p.id, label: p.name }))}
          />
        </Field>
        <div className="grid grid-cols-2 gap-3">
          <Field label="Nom" error={errors.name}>
            <Input value={form.name} onChange={set("name")} />
          </Field>
          <Field label="Adresse" error={errors.street}>
            <Input value={form.street} onChange={set("street")} />
          </Field>
          <Field label="Ville" error={errors.city}>
            <Input value={form.city} onChange={set("city")} />
          </Field>
          <Field label="Code postal" error={errors.zipCode}>
            <Input value={form.zipCode} onChange={set("zipCode")} />
          </Field>
          <Field label="Latitude" error={errors.latitude}>
            <Input
              type="number"
              step="any"
              value={form.latitude}
              onChange={set("latitude")}
            />
          </Field>
          <Field label="Longitude" error={errors.longitude}>
            <Input
              type="number"
              step="any"
              value={form.longitude}
              onChange={set("longitude")}
            />
          </Field>
        </div>
        <Field label="Description">
          <Textarea
            rows={4}
            value={form.description}
            onChange={set("description")}
          />
        </Field>
        <div className="flex flex-col gap-3 rounded-xl border border-border p-3">
          <p className="text-sm font-semibold">Contact</p>
          <div className="grid grid-cols-2 gap-3">
            <Field label="Prénom">
              <Input
                value={form.contactFirstName}
                onChange={set("contactFirstName")}
              />
            </Field>
            <Field label="Nom">
              <Input
                value={form.contactLastName}
                onChange={set("contactLastName")}
              />
            </Field>
            <Field label="Email" error={errors.contactEmail}>
              <Input
                type="email"
                value={form.contactEmail}
                onChange={set("contactEmail")}
              />
            </Field>
            <Field label="Téléphone">
              <Input
                value={form.contactPhoneNumber}
                onChange={set("contactPhoneNumber")}
              />
            </Field>
          </div>
        </div>
        <div className="flex items-center justify-end gap-3">
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

export default function AdminClubs() {
  const dispatch = useDispatch();
  const { clubs, partners, error } = useSelector((s) => s.admin);
  const { run, busy } = useAction();
  const [partnerId, setPartnerId] = useState("");
  const [creating, setCreating] = useState(false);
  const [editing, setEditing] = useState(null);

  useEffect(() => {
    dispatch(fetchClubs());
    dispatch(fetchAllPartners());
  }, [dispatch]);

  const partnerName = (id) => partners.find((p) => p.id === id)?.name ?? "";

  const activePartners = partners.filter((p) => p.status === "ACTIVE");

  const change = (id, open) =>
    run(setClubOpen({ id, open }), open ? "Club ouvert" : "Club fermé");

  const filtered = useMemo(
    () => (partnerId ? clubs.filter((c) => c.partnerId === partnerId) : clubs),
    [clubs, partnerId],
  );

  return (
    <>
      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <div>
            <h1 className="text-2xl font-extrabold text-foreground">Clubs</h1>
            <p className="text-sm text-muted-foreground">
              Les clubs des partenaires
            </p>
          </div>
          <div className="w-64">
            <Select
              placeholder="Tous les partenaires"
              value={partnerId}
              onChange={(e) => setPartnerId(e.target.value)}
              options={activePartners.map((p) => ({
                value: p.id,
                label: p.name,
              }))}
            />
          </div>
        </div>
        <Button onClick={() => setCreating(true)}>+ Nouveau club</Button>
      </div>
      <ClubModal
        open={creating}
        partners={activePartners}
        onClose={() => setCreating(false)}
      />
      {editing && (
        <ClubModal
          open
          club={editing}
          partners={activePartners}
          onClose={() => setEditing(null)}
        />
      )}
      <ErrorBanner message={error} />
      {clubs.length === 0 && <EmptyState>Aucun club.</EmptyState>}
      {clubs.length > 0 && filtered.length === 0 && (
        <EmptyState>Aucun club pour ce partenaire.</EmptyState>
      )}
      {filtered.length > 0 && (
        <div className="overflow-x-auto rounded-2xl border border-border bg-card">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-border text-muted-foreground">
              <tr>
                {[
                  "Nom",
                  "Partenaire",
                  "Adresse",
                  "Coordonnées",
                  "Ouvert",
                  "",
                ].map((h) => (
                  <th key={h} className="px-3 py-3 font-medium">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filtered.map((c) => (
                <tr key={c.id} className="border-b border-border last:border-0">
                  <td className="px-3 py-4 font-medium">{c.name}</td>
                  <td className="px-3 py-4">{partnerName(c.partnerId)}</td>
                  <td className="px-3 py-4 text-muted-foreground">
                    {c.street}, {c.zipCode} {c.city}
                  </td>
                  <td className="px-3 py-4 text-muted-foreground">
                    {c.latitude != null && c.longitude != null
                      ? `${c.latitude}, ${c.longitude}`
                      : "—"}
                  </td>
                  <td className="px-3 py-4">
                    <Badge tone={c.open ? "success" : "neutral"}>
                      {c.open ? "Ouvert" : "Fermé"}
                    </Badge>
                  </td>
                  <td className="px-3 py-4">
                    <div className="flex justify-end gap-1">
                      <Button
                        size="sm"
                        variant="ghost"
                        aria-label="Modifier"
                        title="Modifier"
                        onClick={() => setEditing(c)}
                      >
                        <Pencil className="size-4" />
                      </Button>
                      <Button
                        size="sm"
                        variant="ghost"
                        aria-label={c.open ? "Fermer" : "Ouvrir"}
                        title={c.open ? "Fermer" : "Ouvrir"}
                        disabled={busy}
                        onClick={() => change(c.id, !c.open)}
                      >
                        <Power className="size-4" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}

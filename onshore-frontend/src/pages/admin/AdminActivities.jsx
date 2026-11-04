import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  EmptyState,
  ErrorBanner,
  PageHeader,
} from "../../components/ui/Feedback";
import { Badge } from "../../components/ui/Badge";
import { Button } from "../../components/ui/Button";
import { Field, Input, Select, Textarea } from "../../components/ui/Field";
import { Modal } from "../../components/ui/Modal";
import {
  Ban,
  ChevronLeft,
  ChevronRight,
  Pause,
  Pencil,
  Play,
} from "lucide-react";
import { useAction } from "../../hooks/useAction";
import {
  fetchActivities,
  fetchCategories,
  fetchClubs,
  saveActivity,
  setActivityStatus,
} from "../../Redux/admin/adminSlice";

const STATUS_TONES = {
  ACTIVE: "success",
  SUSPENDED: "warning",
  REJECTED: "danger",
};

const STATUS_LABELS = {
  ACTIVE: "Actif",
  SUSPENDED: "Suspendu",
  REJECTED: "Rejeté",
};

const STATUS_ACTIONS = {
  ACTIVE: [
    {
      to: "SUSPENDED",
      label: "Suspendre",
      icon: Pause,
      toast: "Activité suspendue",
    },
    {
      to: "REJECTED",
      label: "Rejeter",
      icon: Ban,
      toast: "Activité rejetée",
    },
  ],
  SUSPENDED: [
    {
      to: "ACTIVE",
      label: "Réactiver",
      icon: Play,
      toast: "Activité réactivée",
    },
    {
      to: "REJECTED",
      label: "Rejeter",
      icon: Ban,
      toast: "Activité rejetée",
    },
  ],
  REJECTED: [
    {
      to: "ACTIVE",
      label: "Réactiver",
      icon: Play,
      toast: "Activité réactivée",
    },
    {
      to: "SUSPENDED",
      label: "Suspendre",
      icon: Pause,
      toast: "Activité suspendue",
    },
  ],
};

const EMPTY = {
  clubId: "",
  categoryId: "",
  name: "",
  description: "",
  instructorFirstName: "",
  instructorLastName: "",
  instructorEmail: "",
  instructorPhoneNumber: "",
  subscriptionDate: "",
};

function ActivityModal({ open, activity, clubs, categories, onClose }) {
  const { run, busy } = useAction();
  const [form, setForm] = useState(() =>
    activity
      ? {
          clubId: activity.clubId ?? "",
          categoryId: activity.categoryId ?? "",
          name: activity.name ?? "",
          description: activity.description ?? "",
          instructorFirstName: activity.instructorFirstName ?? "",
          instructorLastName: activity.instructorLastName ?? "",
          instructorEmail: activity.instructorEmail ?? "",
          instructorPhoneNumber: activity.instructorPhoneNumber ?? "",
          subscriptionDate: activity.subscriptionDate ?? "",
        }
      : EMPTY,
  );
  const [errors, setErrors] = useState({});

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const close = () => {
    if (!activity) setForm(EMPTY);
    setErrors({});
    onClose();
  };

  const submit = async (event) => {
    event.preventDefault();
    const next = {};
    if (!form.clubId) next.clubId = "Champ obligatoire";
    if (!form.categoryId) next.categoryId = "Champ obligatoire";
    if (!form.name.trim()) next.name = "Champ obligatoire";
    if (form.instructorEmail && !/^\S+@\S+\.\S+$/.test(form.instructorEmail)) {
      next.instructorEmail = "Email invalide";
    }
    setErrors(next);
    if (Object.keys(next).length) return;
    const body = {
      clubId: form.clubId,
      categoryId: form.categoryId,
      name: form.name,
      description: form.description || null,
      instructorFirstName: form.instructorFirstName || null,
      instructorLastName: form.instructorLastName || null,
      instructorEmail: form.instructorEmail || null,
      instructorPhoneNumber: form.instructorPhoneNumber || null,
      subscriptionDate: form.subscriptionDate || null,
    };
    const { ok } = await run(
      activity
        ? saveActivity({ id: activity.id, body })
        : saveActivity({ body }),
      activity ? "Activité mise à jour" : "Activité créée",
    );
    if (ok) close();
  };

  return (
    <Modal
      open={open}
      title={activity ? "Modifier l'activité" : "Nouvelle activité"}
      onClose={close}
    >
      <form onSubmit={submit} className="flex flex-col gap-3">
        <Field label="Club" error={errors.clubId}>
          <Select
            placeholder="Choisir un club"
            value={form.clubId}
            onChange={set("clubId")}
            options={clubs.map((c) => ({ value: c.id, label: c.name }))}
          />
        </Field>
        <Field label="Catégorie" error={errors.categoryId}>
          <Select
            placeholder="Choisir une catégorie"
            value={form.categoryId}
            onChange={set("categoryId")}
            options={categories.map((c) => ({ value: c.id, label: c.name }))}
          />
        </Field>
        <Field label="Nom" error={errors.name}>
          <Input value={form.name} onChange={set("name")} />
        </Field>
        <Field label="Description">
          <Textarea
            rows={4}
            value={form.description}
            onChange={set("description")}
          />
        </Field>
        <div className="flex flex-col gap-3 rounded-xl border border-border p-3">
          <p className="text-sm font-semibold">Instructeur</p>
          <div className="grid grid-cols-2 gap-3">
            <Field label="Prénom">
              <Input
                value={form.instructorFirstName}
                onChange={set("instructorFirstName")}
              />
            </Field>
            <Field label="Nom">
              <Input
                value={form.instructorLastName}
                onChange={set("instructorLastName")}
              />
            </Field>
            <Field label="Email" error={errors.instructorEmail}>
              <Input
                type="email"
                value={form.instructorEmail}
                onChange={set("instructorEmail")}
              />
            </Field>
            <Field label="Téléphone">
              <Input
                value={form.instructorPhoneNumber}
                onChange={set("instructorPhoneNumber")}
              />
            </Field>
          </div>
        </div>
        <Field
          label="Date de souscription"
          hint={activity ? "Non modifiable" : undefined}
        >
          <Input
            type="date"
            readOnly={!!activity}
            value={form.subscriptionDate}
            onChange={set("subscriptionDate")}
          />
        </Field>
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

export default function AdminActivities() {
  const dispatch = useDispatch();
  const {
    activities,
    activitiesPage,
    activitiesPages,
    activitiesTotal,
    clubs,
    categories,
    error,
  } = useSelector((s) => s.admin);
  const [clubId, setClubId] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [status, setStatus] = useState("");
  const [page, setPage] = useState(0);
  const [creating, setCreating] = useState(false);
  const [editing, setEditing] = useState(null);
  const { run, busy } = useAction();

  useEffect(() => {
    dispatch(fetchClubs());
    dispatch(fetchCategories());
  }, [dispatch]);

  useEffect(() => {
    dispatch(
      fetchActivities({
        clubId: clubId || undefined,
        categoryId: categoryId || undefined,
        status: status || undefined,
        page,
        size: 20,
      }),
    );
  }, [dispatch, clubId, categoryId, status, page]);

  const clubName = (id) => clubs.find((c) => c.id === id)?.name ?? "—";
  const categoryName = (id) => categories.find((c) => c.id === id)?.name ?? "—";

  const openClubs = clubs.filter((c) => c.open);
  const activeCategories = categories.filter((c) => c.active);

  const instructorName = (a) => {
    const name = [a.instructorFirstName, a.instructorLastName]
      .filter(Boolean)
      .join(" ");
    return name || "—";
  };

  const hasFilter = clubId || categoryId || status;

  const changeStatus = (activity, action) =>
    run(
      setActivityStatus({ id: activity.id, status: action.to }),
      action.toast,
    );

  return (
    <>
      <PageHeader
        title="Activités"
        subtitle="Les activités proposées par les clubs"
        actions={
          <Button onClick={() => setCreating(true)}>+ Nouvelle activité</Button>
        }
      />
      <ActivityModal
        open={creating}
        clubs={openClubs}
        categories={activeCategories}
        onClose={() => setCreating(false)}
      />
      {editing && (
        <ActivityModal
          open
          activity={editing}
          clubs={openClubs}
          categories={activeCategories}
          onClose={() => setEditing(null)}
        />
      )}
      <div className="mb-4 flex flex-wrap items-center gap-3">
        <div className="w-64">
          <Select
            placeholder="Tous les clubs"
            value={clubId}
            onChange={(e) => {
              setClubId(e.target.value);
              setPage(0);
            }}
            options={openClubs.map((c) => ({ value: c.id, label: c.name }))}
          />
        </div>
        <div className="w-64">
          <Select
            placeholder="Toutes les catégories"
            value={categoryId}
            onChange={(e) => {
              setCategoryId(e.target.value);
              setPage(0);
            }}
            options={activeCategories.map((c) => ({
              value: c.id,
              label: c.name,
            }))}
          />
        </div>
        <div className="w-48">
          <Select
            placeholder="Tous les statuts"
            value={status}
            onChange={(e) => {
              setStatus(e.target.value);
              setPage(0);
            }}
            options={[
              { value: "ACTIVE", label: "Actif" },
              { value: "SUSPENDED", label: "Suspendu" },
              { value: "REJECTED", label: "Rejeté" },
            ]}
          />
        </div>
      </div>
      <ErrorBanner message={error} />
      {activities.length === 0 && (
        <EmptyState>
          {hasFilter
            ? "Aucune activité ne correspond aux filtres."
            : "Aucune activité."}
        </EmptyState>
      )}
      {activities.length > 0 && (
        <div className="overflow-x-auto rounded-2xl border border-border bg-card">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-border text-muted-foreground">
              <tr>
                {["Nom", "Club", "Catégorie", "Instructeur", "Statut", ""].map(
                  (h) => (
                    <th key={h} className="px-3 py-3 font-medium">
                      {h}
                    </th>
                  ),
                )}
              </tr>
            </thead>
            <tbody>
              {activities.map((a) => (
                <tr key={a.id} className="border-b border-border last:border-0">
                  <td className="px-3 py-4 font-medium">{a.name}</td>
                  <td className="px-3 py-4">{clubName(a.clubId)}</td>
                  <td className="px-3 py-4">{categoryName(a.categoryId)}</td>
                  <td className="px-3 py-4 text-muted-foreground">
                    {instructorName(a)}
                  </td>
                  <td className="px-3 py-4">
                    <Badge tone={STATUS_TONES[a.status] ?? "neutral"}>
                      {STATUS_LABELS[a.status] ?? a.status}
                    </Badge>
                  </td>
                  <td className="px-3 py-4">
                    <div className="flex justify-end gap-1">
                      {(STATUS_ACTIONS[a.status] ?? []).map((action) => {
                        const Icon = action.icon;
                        return (
                          <Button
                            key={action.to}
                            size="sm"
                            variant="ghost"
                            aria-label={action.label}
                            title={action.label}
                            disabled={busy}
                            onClick={() => changeStatus(a, action)}
                          >
                            <Icon className="size-4" />
                          </Button>
                        );
                      })}
                      <Button
                        size="sm"
                        variant="ghost"
                        aria-label="Modifier"
                        title="Modifier"
                        onClick={() => setEditing(a)}
                      >
                        <Pencil className="size-4" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {activitiesTotal > 0 && (
        <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
          <p className="text-sm text-muted-foreground">
            {activitiesTotal} activité{activitiesTotal > 1 ? "s" : ""}
          </p>
          {activitiesPages > 1 && (
            <div className="flex items-center gap-2">
              <Button
                size="sm"
                variant="ghost"
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                <ChevronLeft className="size-4" />
                Précédent
              </Button>
              <span className="text-sm text-muted-foreground">
                Page {activitiesPage + 1} / {activitiesPages}
              </span>
              <Button
                size="sm"
                variant="ghost"
                disabled={page >= activitiesPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Suivant
                <ChevronRight className="size-4" />
              </Button>
            </div>
          )}
        </div>
      )}
    </>
  );
}

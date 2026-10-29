import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { EmptyState, ErrorBanner, PageHeader } from "../../components/ui/Feedback";
import { Badge } from "../../components/ui/Badge";
import { fetchUsers } from "../../Redux/admin/adminSlice";

const ROLE_LABEL = { ROLE_MEMBER: "Membre", ROLE_PARTNER: "Partenaire", ROLE_SYSTEM_ADMIN: "Admin" };

export default function AdminMembers() {
  const dispatch = useDispatch();
  const { users, error } = useSelector((s) => s.admin);

  useEffect(() => {
    dispatch(fetchUsers());
  }, [dispatch]);

  return (
    <>
      <PageHeader title="Membres" />
      <ErrorBanner message={error} />
      {users.length === 0 && <EmptyState>Aucun utilisateur.</EmptyState>}
      <ul className="flex flex-col gap-2">
        {users.map((u) => (
          <li key={u.id} className="flex items-center justify-between rounded-xl border border-border bg-card p-3 text-sm">
            <span>
              <span className="block font-semibold">{u.fullName}</span>
              <span className="text-xs text-muted-foreground">{u.email}</span>
            </span>
            <Badge tone="info">{ROLE_LABEL[u.role] ?? u.role}</Badge>
          </li>
        ))}
      </ul>
    </>
  );
}
